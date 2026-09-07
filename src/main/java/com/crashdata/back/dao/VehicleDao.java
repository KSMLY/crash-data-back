package com.crashdata.back.dao;

import com.crashdata.back.code.Manoeuvre;
import com.crashdata.back.code.SpecialFunction;
import com.crashdata.back.code.VehicleType;
import com.crashdata.back.entity.Vehicle;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import static com.crashdata.back.code.CodedEnum.codeOf;

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

    private static final String INSERT = """
            INSERT INTO vehicle (crash_id, vehicle_number, vehicle_type_code,
                                 make, model, model_year, engine_cc,
                                 special_function_code, manoeuvre_code)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""";

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

    public Long insert(Vehicle vehicle) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"});
            ps.setObject(1, vehicle.getCrashId());
            ps.setObject(2, vehicle.getVehicleNumber());
            ps.setObject(3, codeOf(vehicle.getVehicleType()));
            ps.setObject(4, vehicle.getMake());
            ps.setObject(5, vehicle.getModel());
            ps.setObject(6, vehicle.getModelYear());
            ps.setObject(7, vehicle.getEngineCc());
            ps.setObject(8, codeOf(vehicle.getSpecialFunction()));
            ps.setObject(9, codeOf(vehicle.getManoeuvre()));
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
