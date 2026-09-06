package com.crashdata.back.dao;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.Person;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.crashdata.back.code.CodedEnum.codeOf;

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

    private static final String INSERT = """
            INSERT INTO person (crash_id, person_number, occupant_vehicle_id, struck_by_vehicle_id, date_of_birth,
                                sex_code, road_user_type_code, seat_row_code, seat_position_code, injury_severity_code,
                                restraint_code, helmet_code, ped_manoeuvre_code, alcohol_suspected_code, drug_use_code,
                                licence_status_code, licence_issue_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

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

    public Long insert(Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"});
            ps.setObject(1, person.getCrashId());
            ps.setObject(2, person.getPersonNumber());
            ps.setObject(3, person.getOccupantVehicleId());
            ps.setObject(4, person.getStruckByVehicleId());
            ps.setObject(5, person.getDateOfBirth());
            ps.setObject(6, codeOf(person.getSex()));
            ps.setObject(7, codeOf(person.getRoadUserType()));
            ps.setObject(8, codeOf(person.getSeatRow()));
            ps.setObject(9, codeOf(person.getSeatPosition()));
            ps.setObject(10, codeOf(person.getInjurySeverity()));
            ps.setObject(11, codeOf(person.getRestraint()));
            ps.setObject(12, codeOf(person.getHelmet()));
            ps.setObject(13, codeOf(person.getPedManoeuvre()));
            ps.setObject(14, codeOf(person.getAlcoholSuspected()));
            ps.setObject(15, codeOf(person.getDrugUse()));
            ps.setObject(16, codeOf(person.getLicenceStatus()));
            ps.setObject(17, person.getLicenceIssueDate());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
