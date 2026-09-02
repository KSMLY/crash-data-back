package com.crashdata.back.dao;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.Person;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@AllArgsConstructor
public class PersonDao {

    private static final String FIND_BY_CRASH = """
            SELECT id, crash_id, person_number, occupant_vehicle_id, struck_by_vehicle_id, date_of_birth,
                   sex_code, road_user_type_code, seat_row_code, seat_position_code, injury_severity_code,
                   restraint_code, helmet_code, ped_manoeuvre_code, alcohol_suspected_code, drug_use_code,
                   licence_status_code, licence_issue_date
            FROM person
            WHERE crash_id = ?
            ORDER BY person_number""";

    private static final RowMapper<Person> ROW_MAPPER = (rs, rowNum) -> new Person(
            rs.getLong("id"),
            rs.getLong("crash_id"),
            rs.getShort("person_number"),
            rs.getObject("occupant_vehicle_id", Long.class),
            rs.getObject("struck_by_vehicle_id", Long.class),
            rs.getObject("date_of_birth", LocalDate.class),
            Codes.of(rs, "sex_code", Sex.class),
            Codes.of(rs, "road_user_type_code", RoadUserType.class),
            Codes.of(rs, "seat_row_code", SeatRow.class),
            Codes.of(rs, "seat_position_code", SeatPosition.class),
            Codes.of(rs, "injury_severity_code", InjurySeverity.class),
            Codes.of(rs, "restraint_code", Restraint.class),
            Codes.of(rs, "helmet_code", Helmet.class),
            Codes.of(rs, "ped_manoeuvre_code", PedManoeuvre.class),
            Codes.of(rs, "alcohol_suspected_code", AlcoholSuspected.class),
            Codes.of(rs, "drug_use_code", DrugUse.class),
            Codes.of(rs, "licence_status_code", LicenceStatus.class),
            rs.getObject("licence_issue_date", LocalDate.class));

    private final JdbcTemplate jdbcTemplate;

    public List<Person> findByCrashId(Long crashId) {
        return jdbcTemplate.query(FIND_BY_CRASH, ROW_MAPPER, crashId);
    }
}
