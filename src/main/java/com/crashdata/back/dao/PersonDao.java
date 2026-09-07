package com.crashdata.back.dao;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.Person;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.crashdata.back.code.CodedEnum.codeOf;

@Repository
@AllArgsConstructor
public class PersonDao {

    private static final String FIND_BY_CRASH = Sql.load("sql/person/find-by-crash.sql");
    private static final String INSERT = Sql.load("sql/person/insert.sql");

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

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Person> findByCrashId(Long crashId) {
        return jdbcTemplate.query(FIND_BY_CRASH, new MapSqlParameterSource("crash_id", crashId), ROW_MAPPER);
    }

    public Long insert(Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT, parameters(person), keyHolder, new String[]{"id"});
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private static SqlParameterSource parameters(Person person) {
        return new MapSqlParameterSource()
                .addValue("crash_id", person.getCrashId())
                .addValue("person_number", person.getPersonNumber())
                .addValue("occupant_vehicle_id", person.getOccupantVehicleId())
                .addValue("struck_by_vehicle_id", person.getStruckByVehicleId())
                .addValue("date_of_birth", person.getDateOfBirth())
                .addValue("sex_code", codeOf(person.getSex()))
                .addValue("road_user_type_code", codeOf(person.getRoadUserType()))
                .addValue("seat_row_code", codeOf(person.getSeatRow()))
                .addValue("seat_position_code", codeOf(person.getSeatPosition()))
                .addValue("injury_severity_code", codeOf(person.getInjurySeverity()))
                .addValue("restraint_code", codeOf(person.getRestraint()))
                .addValue("helmet_code", codeOf(person.getHelmet()))
                .addValue("ped_manoeuvre_code", codeOf(person.getPedManoeuvre()))
                .addValue("alcohol_suspected_code", codeOf(person.getAlcoholSuspected()))
                .addValue("drug_use_code", codeOf(person.getDrugUse()))
                .addValue("licence_status_code", codeOf(person.getLicenceStatus()))
                .addValue("licence_issue_date", person.getLicenceIssueDate());
    }
}
