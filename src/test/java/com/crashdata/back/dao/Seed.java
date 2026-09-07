package com.crashdata.back.dao;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Parent rows the child-table tests need but do not assert on. Everything is
 * written with plain SQL rather than the DAOs, so a DAO under test is never
 * used to set up its own fixture.
 */
final class Seed {

    private Seed() {
    }

    /** Any district Flyway seeded — crash.district_id is NOT NULL with a foreign key. */
    static long districtId(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForObject("SELECT MIN(id) FROM district", Long.class);
    }

    static long crash(JdbcTemplate jdbcTemplate, String policeRef, long districtId) {
        jdbcTemplate.update("""
                INSERT INTO crash (police_ref, ref_year, district_id,
                                   crash_type_code, impact_type_code, weather_code, light_code,
                                   severity_code, roadway_type_code, speed_limit_kmh,
                                   obstacle_present_code, surface_condition_code,
                                   junction_type_code, curve_code, grade_code)
                VALUES (?, 2024, ?, 5, 10, 4, 2, 3, 6, 80, 9, 5, 6, 2, 2)""",
                policeRef, districtId);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM crash WHERE ref_year = 2024 AND police_ref = ?", Long.class, policeRef);
    }

    static long vehicle(JdbcTemplate jdbcTemplate, long crashId, int vehicleNumber) {
        jdbcTemplate.update("""
                INSERT INTO vehicle (crash_id, vehicle_number, vehicle_type_code,
                                     special_function_code, manoeuvre_code)
                VALUES (?, ?, 4, 1, 13)""",
                crashId, vehicleNumber);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM vehicle WHERE crash_id = ? AND vehicle_number = ?",
                Long.class, crashId, vehicleNumber);
    }

    static long person(JdbcTemplate jdbcTemplate, long crashId, int personNumber) {
        jdbcTemplate.update("""
                INSERT INTO person (crash_id, person_number, sex_code, road_user_type_code,
                                    seat_row_code, seat_position_code, injury_severity_code,
                                    restraint_code, helmet_code, alcohol_suspected_code, drug_use_code)
                VALUES (?, ?, 1, 1, 1, 1, 4, 1, 3, 1, 1)""",
                crashId, personNumber);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM person WHERE crash_id = ? AND person_number = ?",
                Long.class, crashId, personNumber);
    }
}
