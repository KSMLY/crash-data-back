CREATE TABLE crash (
                       id                  INT IDENTITY(1,1) PRIMARY KEY,
                       crash_ref           NVARCHAR(20),
                       occurred_at         DATETIME2      NOT NULL,
                       municipality        NVARCHAR(100),
                       region              NVARCHAR(100),
                       latitude            DECIMAL(9,6),
                       longitude           DECIMAL(9,6),
                       crash_type          TINYINT,
                       impact_type         TINYINT,
                       weather             TINYINT,
                       light_condition     TINYINT,
                       severity            TINYINT        NOT NULL,
                       vehicles_involved   INT,
                       created_at          DATETIME2      NOT NULL DEFAULT SYSUTCDATETIME()
);