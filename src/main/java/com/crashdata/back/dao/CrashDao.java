package com.crashdata.back.dao;

import com.crashdata.back.code.CodedEnum;
import com.crashdata.back.code.TrafficControl;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.Municipality;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.crashdata.back.code.CodedEnum.codeOf;

@Repository
@AllArgsConstructor
public class CrashDao {

    private static final String FIND_ALL = Sql.load("sql/crash/find-all.sql");
    private static final String FIND_BY_ID = Sql.load("sql/crash/find-by-id.sql");
    private static final String INSERT = Sql.load("sql/crash/insert.sql");
    private static final String INSERT_CONTROL = Sql.load("sql/crash/insert-control.sql");
    private static final String CONTROLS = Sql.load("sql/crash/controls.sql");
    private static final String CONTROLS_BY_CRASH = Sql.load("sql/crash/controls-by-crash.sql");

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Crash> findAll() {
        return jdbcTemplate.query(FIND_ALL,
                new CrashRowMapper(loadControls(CONTROLS, new MapSqlParameterSource())));
    }

    public Optional<Crash> findById(Long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(FIND_BY_ID, params,
                        new CrashRowMapper(loadControls(CONTROLS_BY_CRASH, new MapSqlParameterSource("crash_id", id))))
                .stream()
                .findFirst();
    }

    public Long insert(Crash crash) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT, parameters(crash), keyHolder, new String[]{"id"});

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        insertControls(id, crash.getTrafficControls());
        return id;
    }

    private static SqlParameterSource parameters(Crash crash) {
        return new MapSqlParameterSource()
                .addValue("police_ref", crash.getPoliceRef())
                .addValue("ref_year", crash.getRefYear())
                .addValue("crash_date", crash.getCrashDate())
                .addValue("crash_time", crash.getCrashTime())
                .addValue("district_id", crash.getDistrict().getId())
                .addValue("municipality_id", idOf(crash.getMunicipality()))
                .addValue("latitude", crash.getLatitude())
                .addValue("longitude", crash.getLongitude())
                .addValue("crash_type_code", codeOf(crash.getCrashType()))
                .addValue("impact_type_code", codeOf(crash.getImpactType()))
                .addValue("weather_code", codeOf(crash.getWeather()))
                .addValue("light_code", codeOf(crash.getLight()))
                .addValue("severity_code", codeOf(crash.getSeverity()))
                .addValue("roadway_type_code", codeOf(crash.getRoadwayType()))
                .addValue("functional_class_code", codeOf(crash.getFunctionalClass()))
                .addValue("speed_limit_kmh", crash.getSpeedLimitKmh())
                .addValue("obstacle_present_code", codeOf(crash.getObstaclePresent()))
                .addValue("surface_condition_code", codeOf(crash.getSurfaceCondition()))
                .addValue("junction_type_code", codeOf(crash.getJunctionType()))
                .addValue("curve_code", codeOf(crash.getCurve()))
                .addValue("grade_code", codeOf(crash.getGrade()));
    }

    private static Long idOf(Municipality municipality) {
        return municipality == null ? null : municipality.getId();
    }

    private void insertControls(long crashId, Set<TrafficControl> controls) {
        if (controls == null || controls.isEmpty()) return;
        SqlParameterSource[] batch = controls.stream()
                .map(control -> (SqlParameterSource) new MapSqlParameterSource()
                        .addValue("crash_id", crashId)
                        .addValue("control_code", control.getCode()))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(INSERT_CONTROL, batch);
    }

    private Map<Long, Set<TrafficControl>> loadControls(String sql, SqlParameterSource params) {
        Map<Long, Set<TrafficControl>> byCrashId = new HashMap<>();
        RowCallbackHandler collect = rs ->
                byCrashId.computeIfAbsent(rs.getLong("crash_id"), key -> new HashSet<>())
                        .add(CodedEnum.fromCode(TrafficControl.class, rs.getShort("control_code")));
        jdbcTemplate.query(sql, params, collect);
        return byCrashId;
    }
}
