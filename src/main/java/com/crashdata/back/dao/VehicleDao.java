package com.crashdata.back.dao;

import com.crashdata.back.code.Manoeuvre;
import com.crashdata.back.code.SpecialFunction;
import com.crashdata.back.code.VehicleType;
import com.crashdata.back.entity.Vehicle;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.crashdata.back.code.CodedEnum.codeOf;

@Repository
@AllArgsConstructor
public class VehicleDao {

    private static final String FIND_BY_CRASH = Sql.load("sql/vehicle/find-by-crash.sql");
    private static final String INSERT = Sql.load("sql/vehicle/insert.sql");

    private static final RowMapper<Vehicle> ROW_MAPPER = (rs, rowNum) -> new Vehicle(
            rs.getLong("id"),
            rs.getLong("crash_id"),
            rs.getShort("vehicle_number"),
            Codes.of(rs, "vehicle_type_code", VehicleType.class),
            rs.getString("make"),
            rs.getString("model"),
            rs.getObject("model_year", Short.class),
            rs.getObject("engine_cc", Integer.class),
            Codes.of(rs, "special_function_code", SpecialFunction.class),
            Codes.of(rs, "manoeuvre_code", Manoeuvre.class));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Vehicle> findByCrashId(Long crashId) {
        return jdbcTemplate.query(FIND_BY_CRASH, new MapSqlParameterSource("crash_id", crashId), ROW_MAPPER);
    }

    public Long insert(Vehicle vehicle) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT, parameters(vehicle), keyHolder, new String[]{"id"});
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private static SqlParameterSource parameters(Vehicle vehicle) {
        return new MapSqlParameterSource()
                .addValue("crash_id", vehicle.getCrashId())
                .addValue("vehicle_number", vehicle.getVehicleNumber())
                .addValue("vehicle_type_code", codeOf(vehicle.getVehicleType()))
                .addValue("make", vehicle.getMake())
                .addValue("model", vehicle.getModel())
                .addValue("model_year", vehicle.getModelYear())
                .addValue("engine_cc", vehicle.getEngineCc())
                .addValue("special_function_code", codeOf(vehicle.getSpecialFunction()))
                .addValue("manoeuvre_code", codeOf(vehicle.getManoeuvre()));
    }
}
