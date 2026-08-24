package com.crashdata.back.dao;

import com.crashdata.back.code.CodedEnum;
import com.crashdata.back.code.TrafficControl;
import com.crashdata.back.entity.Crash;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public class CrashDao {

    private static final String BASE = """
            SELECT id, police_ref, ref_year, crash_date, crash_time,
                   district_id, municipality_id, latitude, longitude,
                   crash_type_code, impact_type_code, weather_code, light_code, severity_code,
                   roadway_type_code, functional_class_code, speed_limit_kmh,
                   obstacle_present_code, surface_condition_code, junction_type_code,
                   curve_code, grade_code
            FROM crash""";

    private static final String FIND_ALL =
            BASE + " ORDER BY ref_year, police_ref";

    private static final String FIND_BY_ID =
            BASE + " WHERE id = ?";

    private static final String INSERT = """
            INSERT INTO crash (police_ref, ref_year, crash_date, crash_time,
                               district_id, municipality_id, latitude, longitude,
                               crash_type_code, impact_type_code, weather_code, light_code, severity_code,
                               roadway_type_code, functional_class_code, speed_limit_kmh,
                               obstacle_present_code, surface_condition_code, junction_type_code,
                               curve_code, grade_code)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

    private static final String INSERT_CONTROL =
            "INSERT INTO crash_traffic_control (crash_id, control_code) VALUES (?, ?)";

    private static final String CONTROLS =
            "SELECT crash_id, control_code FROM crash_traffic_control";

    private static final String CONTROLS_BY_CRASH =
            CONTROLS + " WHERE crash_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public CrashDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Crash> findAll() {
        return jdbcTemplate.query(FIND_ALL, new CrashRowMapper(loadControls(CONTROLS)));
    }

    public Optional<Crash> findById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID, new CrashRowMapper(loadControls(CONTROLS_BY_CRASH, id)), id)
                .stream()
                .findFirst();
    }

    public Long insert(Crash crash) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"});
            ps.setObject(1, crash.getPoliceRef());
            ps.setObject(2, crash.getRefYear());
            ps.setObject(3, crash.getCrashDate());
            ps.setObject(4, crash.getCrashTime());
            ps.setObject(5, crash.getDistrictId());
            ps.setObject(6, crash.getMunicipalityId());
            ps.setObject(7, crash.getLatitude());
            ps.setObject(8, crash.getLongitude());
            ps.setObject(9, code(crash.getCrashType()));
            ps.setObject(10, code(crash.getImpactType()));
            ps.setObject(11, code(crash.getWeather()));
            ps.setObject(12, code(crash.getLight()));
            ps.setObject(13, code(crash.getSeverity()));
            ps.setObject(14, code(crash.getRoadwayType()));
            ps.setObject(15, code(crash.getFunctionalClass()));
            ps.setObject(16, crash.getSpeedLimitKmh());
            ps.setObject(17, code(crash.getObstaclePresent()));
            ps.setObject(18, code(crash.getSurfaceCondition()));
            ps.setObject(19, code(crash.getJunctionType()));
            ps.setObject(20, code(crash.getCurve()));
            ps.setObject(21, code(crash.getGrade()));
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        insertControls(id, crash.getTrafficControls());
        return id;
    }

    private void insertControls(long crashId, Set<TrafficControl> controls) {
        if (controls == null || controls.isEmpty()) return;
        List<Object[]> rows = controls.stream()
                .map(control -> new Object[]{crashId, control.getCode()})
                .toList();
        jdbcTemplate.batchUpdate(INSERT_CONTROL, rows);
    }

    private static Short code(CodedEnum value) {
        return value == null ? null : value.getCode();
    }

    private Map<Long, Set<TrafficControl>> loadControls(String sql, Object... args) {
        Map<Long, Set<TrafficControl>> byCrashId = new HashMap<>();
        RowCallbackHandler collect = rs ->
                byCrashId.computeIfAbsent(rs.getLong("crash_id"), key -> new HashSet<>())
                        .add(CodedEnum.fromCode(TrafficControl.class, rs.getShort("control_code")));
        jdbcTemplate.query(sql, collect, args);
        return byCrashId;
    }
}
