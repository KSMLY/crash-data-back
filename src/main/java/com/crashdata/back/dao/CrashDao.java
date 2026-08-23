package com.crashdata.back.dao;

import com.crashdata.back.code.CodedEnum;
import com.crashdata.back.code.TrafficControl;
import com.crashdata.back.entity.Crash;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    private Map<Long, Set<TrafficControl>> loadControls(String sql, Object... args) {
        Map<Long, Set<TrafficControl>> byCrashId = new HashMap<>();
        RowCallbackHandler collect = rs ->
                byCrashId.computeIfAbsent(rs.getLong("crash_id"), key -> new HashSet<>())
                        .add(CodedEnum.fromCode(TrafficControl.class, rs.getShort("control_code")));
        jdbcTemplate.query(sql, collect, args);
        return byCrashId;
    }
}
