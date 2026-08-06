/*
 * V2 — crash data schema
 *
 * Replaces the provisional crash table from V1.
 *
 * See docs/decisions.md for why the schema is shaped this way and
 * docs/codes.md for the meaning of every code column.
 *
 * 9 inside code lists, 999 for speed limit, 9999 for engine size. NULL is
 * used only where the spec provides no sentinel — dates, not-applicable
 * fields, and columns expected to be filled by linkage later.
 *
 * Code lists are enforced in Java enums, not in the database.
 */


/* ---------------------------------------------------------------------
 * Drop in reverse dependency order.
 * Safe here because no data exists yet. Once this schema holds real rows,
 * later changes must be ALTER statements in a new migration.
 * ------------------------------------------------------------------ */

DROP TABLE IF EXISTS alcohol_test;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS crash_traffic_control;
DROP TABLE IF EXISTS crash;
DROP TABLE IF EXISTS municipality;
DROP TABLE IF EXISTS district;
DROP TABLE IF EXISTS governorate;
GO


/* ---------------------------------------------------------------------
 * Administrative hierarchy
 * governorate -> district -> municipality
 * Seeded separately in V3.
 * ------------------------------------------------------------------ */

CREATE TABLE governorate (
                             id       BIGINT        IDENTITY(1,1) NOT NULL,
                             name_en  NVARCHAR(100) NOT NULL,
                             name_ar  NVARCHAR(100) NOT NULL,

                             CONSTRAINT pk_governorate PRIMARY KEY (id)
);
GO

CREATE TABLE district (
                          id              BIGINT        IDENTITY(1,1) NOT NULL,
                          governorate_id  BIGINT        NOT NULL,
                          name_en         NVARCHAR(100) NOT NULL,
                          name_ar         NVARCHAR(100) NOT NULL,

                          CONSTRAINT pk_district           PRIMARY KEY (id),
                          CONSTRAINT fk_district_governorate
                              FOREIGN KEY (governorate_id) REFERENCES governorate (id)
);
GO

CREATE INDEX ix_district_governorate ON district (governorate_id);
GO

CREATE TABLE municipality (
                              id           BIGINT        IDENTITY(1,1) NOT NULL,
                              district_id  BIGINT        NOT NULL,
                              name_en      NVARCHAR(150) NOT NULL,
                              name_ar      NVARCHAR(150) NOT NULL,

                              CONSTRAINT pk_municipality        PRIMARY KEY (id),
                              CONSTRAINT fk_municipality_district
                                  FOREIGN KEY (district_id) REFERENCES district (id)
);
GO

CREATE INDEX ix_municipality_district ON municipality (district_id);
GO


/* ---------------------------------------------------------------------
 * crash
 * The event. Road conditions R1-R9 are merged in here (decision 2);
 * R7 is multi-valued and lives in crash_traffic_control (decision 3).
 * ------------------------------------------------------------------ */

CREATE TABLE crash (
                       id                       BIGINT        IDENTITY(1,1) NOT NULL,

    /* C1 - assigned by police, unique within a year */
                       police_ref               NVARCHAR(30)  NOT NULL,
                       ref_year                 SMALLINT      NOT NULL,

    /* C2, C3 - NULL when unknown; the spec's 99 convention cannot live
       in a DATE column. Partial dates are not representable. */
                       crash_date               DATE          NULL,
                       crash_time               TIME(0)       NULL,

    /* C4 - district always known, municipality absent where no
       municipality exists (mukhtar-led villages) */
                       district_id              BIGINT        NOT NULL,
                       municipality_id          BIGINT        NULL,

    /* C5 */
                       latitude                 DECIMAL(9,6)  NULL,
                       longitude                DECIMAL(9,6)  NULL,

    /* C6-C9 */
                       crash_type_code          SMALLINT      NOT NULL,
                       impact_type_code         SMALLINT      NOT NULL,
                       weather_code             SMALLINT      NOT NULL,
                       light_code               SMALLINT      NOT NULL,

    /* CD1 - derived from person.injury_severity_code, recomputed by the
       service layer whenever a person row for this crash changes */
                       severity_code            SMALLINT      NOT NULL,

    /* R1-R9, excluding R7 */
                       roadway_type_code        SMALLINT      NOT NULL,
                       functional_class_code    SMALLINT      NULL,      -- R2 has no unknown value
                       speed_limit_kmh          SMALLINT      NOT NULL,  -- 999 = unknown
                       obstacle_present_code    SMALLINT      NOT NULL,
                       surface_condition_code   SMALLINT      NOT NULL,
                       junction_type_code       SMALLINT      NOT NULL,
                       curve_code               SMALLINT      NOT NULL,
                       grade_code               SMALLINT      NOT NULL,

                       CONSTRAINT pk_crash            PRIMARY KEY (id),
                       CONSTRAINT uq_crash_police_ref UNIQUE (ref_year, police_ref),
                       CONSTRAINT fk_crash_district
                           FOREIGN KEY (district_id)     REFERENCES district (id),
                       CONSTRAINT fk_crash_municipality
                           FOREIGN KEY (municipality_id) REFERENCES municipality (id)
);
GO

CREATE INDEX ix_crash_district     ON crash (district_id);
CREATE INDEX ix_crash_municipality ON crash (municipality_id);
CREATE INDEX ix_crash_date         ON crash (crash_date);
CREATE INDEX ix_crash_severity     ON crash (severity_code);
GO


/* ---------------------------------------------------------------------
 * crash_traffic_control
 * R7 only. Multi-select: "record all that apply".
 * ------------------------------------------------------------------ */

CREATE TABLE crash_traffic_control (
                                       crash_id      BIGINT   NOT NULL,
                                       control_code  SMALLINT NOT NULL,

                                       CONSTRAINT pk_crash_traffic_control PRIMARY KEY (crash_id, control_code),
                                       CONSTRAINT fk_ctc_crash
                                           FOREIGN KEY (crash_id) REFERENCES crash (id) ON DELETE CASCADE
);
GO


/* ---------------------------------------------------------------------
 * vehicle
 * V1 vehicle_number is sequential within the crash only.
 * ------------------------------------------------------------------ */

CREATE TABLE vehicle (
                         id                     BIGINT       IDENTITY(1,1) NOT NULL,
                         crash_id               BIGINT       NOT NULL,
                         vehicle_number         SMALLINT     NOT NULL,          -- V1

                         vehicle_type_code      SMALLINT     NOT NULL,          -- V2

    /* V3-V6 - NULL when the vehicle has no engine (type 1 or 2).
       engine_cc uses 9999 when motorised but size unknown. */
                         make                   NVARCHAR(50) NULL,
                         model                  NVARCHAR(50) NULL,
                         model_year             SMALLINT     NULL,
                         engine_cc              INT          NULL,

                         special_function_code  SMALLINT     NOT NULL,          -- V7
                         manoeuvre_code         SMALLINT     NOT NULL,          -- V8

                         CONSTRAINT pk_vehicle        PRIMARY KEY (id),
                         CONSTRAINT uq_vehicle_number UNIQUE (crash_id, vehicle_number),
                         CONSTRAINT fk_vehicle_crash
                             FOREIGN KEY (crash_id) REFERENCES crash (id) ON DELETE CASCADE
);
GO

CREATE INDEX ix_vehicle_crash ON vehicle (crash_id);
GO


/* ---------------------------------------------------------------------
 * person
 * Two links to vehicle: the vehicle ridden in (P2) and the vehicle that
 * struck the person (P3). At most one applies; a pedestrian in a single
 * vehicle crash may have neither. Mutual exclusivity is enforced in the
 * service layer.
 *
 * crash_id is carried directly rather than reached through vehicle,
 * because pedestrians may link to no vehicle at all.
 * ------------------------------------------------------------------ */

CREATE TABLE person (
                        id                      BIGINT   IDENTITY(1,1) NOT NULL,
                        crash_id                BIGINT   NOT NULL,
                        person_number           SMALLINT NOT NULL,          -- P1

                        occupant_vehicle_id     BIGINT   NULL,              -- P2
                        struck_by_vehicle_id    BIGINT   NULL,              -- P3

                        date_of_birth           DATE     NULL,              -- P4, NULL if unknown
                        sex_code                SMALLINT NOT NULL,          -- P5
                        road_user_type_code     SMALLINT NOT NULL,          -- P6
                        seat_row_code           SMALLINT NOT NULL,          -- P7.1
                        seat_position_code      SMALLINT NOT NULL,          -- P7.2
                        injury_severity_code    SMALLINT NOT NULL,          -- P8
                        restraint_code          SMALLINT NOT NULL,          -- P9.1
                        helmet_code             SMALLINT NOT NULL,          -- P9.2
                        ped_manoeuvre_code      SMALLINT NULL,              -- P10, pedestrians only
                        alcohol_suspected_code  SMALLINT NOT NULL,          -- P11
                        drug_use_code           SMALLINT NOT NULL,          -- P13

    /* P14 - local codes, see docs/codes.md. NULL for non-drivers.
       Date is NULL unless status = 1. Spec wants month and year only;
       day is stored as 1 and should not be displayed. */
                        licence_status_code     SMALLINT NULL,
                        licence_issue_date      DATE     NULL,

                        CONSTRAINT pk_person        PRIMARY KEY (id),
                        CONSTRAINT uq_person_number UNIQUE (crash_id, person_number),
                        CONSTRAINT fk_person_crash
                            FOREIGN KEY (crash_id) REFERENCES crash (id) ON DELETE CASCADE,
                        CONSTRAINT fk_person_occupant_vehicle
                            FOREIGN KEY (occupant_vehicle_id)  REFERENCES vehicle (id),
                        CONSTRAINT fk_person_struck_by_vehicle
                            FOREIGN KEY (struck_by_vehicle_id) REFERENCES vehicle (id)
);
GO

CREATE INDEX ix_person_crash            ON person (crash_id);
CREATE INDEX ix_person_occupant_vehicle ON person (occupant_vehicle_id);
CREATE INDEX ix_person_struck_by        ON person (struck_by_vehicle_id);
CREATE INDEX ix_person_injury_severity  ON person (injury_severity_code);
GO


/* ---------------------------------------------------------------------
 * alcohol_test
 * P12. Conditional on alcohol being suspected (P11 = 2), so most people
 * have no row. Primary key is also the foreign key: at most one per person.
 * ------------------------------------------------------------------ */

CREATE TABLE alcohol_test (
                              person_id           BIGINT        NOT NULL,
                              test_status_code    SMALLINT      NOT NULL,      -- P12.1
                              test_type_code      SMALLINT      NOT NULL,      -- P12.2

    /* P12.3 - local codes, see docs/codes.md.
       result_value is NULL unless result_status_code = 1. */
                              result_status_code  SMALLINT      NOT NULL,
                              result_value        DECIMAL(5,3)  NULL,

                              CONSTRAINT pk_alcohol_test PRIMARY KEY (person_id),
                              CONSTRAINT fk_alcohol_test_person
                                  FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE
);
GO