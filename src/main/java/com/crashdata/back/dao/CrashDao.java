package com.crashdata.back.dao;

import com.crashdata.back.code.CodedEnum;
import com.crashdata.back.code.CrashSeverity;
import com.crashdata.back.code.TrafficControl;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.CrashPoint;
import com.crashdata.back.entity.CrashSearch;
import com.crashdata.back.entity.Municipality;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    private static final String SEARCH = Sql.load("sql/crash/search.sql");
    private static final String COUNT = Sql.load("sql/crash/count.sql");
    private static final String CONTROLS_BY_CRASHES = Sql.load("sql/crash/controls-by-crashes.sql");
    private static final String FIND_BY_ID = Sql.load("sql/crash/find-by-id.sql");
    private static final String INSERT = Sql.load("sql/crash/insert.sql");
    private static final String INSERT_CONTROL = Sql.load("sql/crash/insert-control.sql");
    private static final String CONTROLS_BY_CRASH = Sql.load("sql/crash/controls-by-crash.sql");
    private static final String POINTS = Sql.load("sql/crash/points.sql");

    // Sort keys the API accepts, mapped to the ORDER BY they expand to. ORDER BY cannot take
    // a bind parameter, so the column is appended to the statement from this whitelist only
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "crashDate", "crash_date %1$s, crash_time %1$s",
            "severity", "severity_code %1$s",
            "policeRef", "ref_year %1$s, police_ref %1$s");

    private static final RowMapper<CrashPoint> POINT_MAPPER = (rs, rowNum) -> new CrashPoint(
            rs.getLong("id"),
            rs.getString("police_ref"),
            rs.getObject("crash_date", LocalDate.class),
            rs.getBigDecimal("latitude"),
            rs.getBigDecimal("longitude"),
            Codes.of(rs, "severity_code", CrashSeverity.class)
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Crash> search(CrashSearch search) {
        String sql = SEARCH + orderBy(search) + " OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY";
        MapSqlParameterSource params = parameters(search)
                .addValue("offset", (long) search.page() * search.size())
                .addValue("size", search.size());

        // Controls are attached after the rows are read, so only this page's controls are loaded
        List<Crash> crashes = jdbcTemplate.query(sql, params, new CrashRowMapper(Map.of()));
        if (crashes.isEmpty()) return crashes;

        List<Long> ids = crashes.stream().map(Crash::getId).toList();
        Map<Long, Set<TrafficControl>> controls =
                loadControls(CONTROLS_BY_CRASHES, new MapSqlParameterSource("crash_ids", ids));
        return crashes.stream()
                .map(crash -> crash.withTrafficControls(controls.getOrDefault(crash.getId(), Set.of())))
                .toList();
    }

    public long count(CrashSearch search) {
        return Objects.requireNonNull(jdbcTemplate.queryForObject(COUNT, parameters(search), Long.class));
    }

    private static String orderBy(CrashSearch search) {
        String columns = SORT_COLUMNS.get(search.sort());
        if (columns == null) {
            throw new IllegalArgumentException("Unknown sort key: " + search.sort());
        }
        String direction = search.descending() ? "DESC" : "ASC";
        // id last so the order is stable across pages when the sort column ties
        return " ORDER BY " + String.format(columns, direction) + ", c.id " + direction;
    }

    private static MapSqlParameterSource parameters(CrashSearch search) {
        return new MapSqlParameterSource()
                .addValue("q", search.q() == null ? null : search.q() + "%")
                .addValue("severity", codeOf(search.severity()))
                .addValue("crash_type", codeOf(search.crashType()))
                .addValue("district_id", search.districtId())
                .addValue("municipality_id", search.municipalityId())
                .addValue("from_date", search.from())
                .addValue("to_date", search.to());
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

    public List<CrashPoint> points(LocalDate from, LocalDate to) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from_date", from)
                .addValue("to_date", to);
        return jdbcTemplate.query(POINTS, params, POINT_MAPPER);
    }
}
