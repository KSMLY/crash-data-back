package com.crashdata.back.dao;

import com.crashdata.back.code.Manoeuvre;
import com.crashdata.back.code.SpecialFunction;
import com.crashdata.back.code.VehicleType;
import com.crashdata.back.entity.Vehicle;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class VehicleDao {

    private static final String FIND_BY_CRASH = """
            SELECT id, crash_id, vehicle_number, vehicle_type_code,
                   make, model, model_year, engine_cc,
                   special_function_code, manoeuvre_code
            FROM vehicle
            WHERE crash_id = ?
            ORDER BY vehicle_number""";

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

    private final JdbcTemplate jdbcTemplate;

    public List<Vehicle> findByCrashId(Long crashId) {
        return jdbcTemplate.query(FIND_BY_CRASH, ROW_MAPPER, crashId);
    }
}
