package com.crashdata.back.dao;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.Crash;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class CrashRowMapper implements RowMapper<Crash> {

    private final Map<Long, Set<TrafficControl>> controlsByCrashId;

    @Override
    public Crash mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        return new Crash(
                id,
                rs.getString("police_ref"),
                rs.getShort("ref_year"),
                rs.getObject("crash_date", LocalDate.class),
                rs.getObject("crash_time", LocalTime.class),
                rs.getLong("district_id"),
                rs.getObject("municipality_id", Long.class),
                rs.getBigDecimal("latitude"),
                rs.getBigDecimal("longitude"),
                code(rs, "crash_type_code", CrashType.class),
                code(rs, "impact_type_code", ImpactType.class),
                code(rs, "weather_code", Weather.class),
                code(rs, "light_code", Light.class),
                code(rs, "severity_code", CrashSeverity.class),
                code(rs, "roadway_type_code", RoadwayType.class),
                code(rs, "functional_class_code", FunctionalClass.class),
                rs.getShort("speed_limit_kmh"),
                code(rs, "obstacle_present_code", ObstaclePresent.class),
                code(rs, "surface_condition_code", SurfaceCondition.class),
                code(rs, "junction_type_code", JunctionType.class),
                code(rs, "curve_code", Curve.class),
                code(rs, "grade_code", Grade.class),
                controlsByCrashId.getOrDefault(id, Set.of()));
    }

    private static <E extends Enum<E> & CodedEnum> E code(ResultSet rs, String column, Class<E> type)
            throws SQLException {
        Short value = rs.getObject(column, Short.class);
        return value == null ? null : CodedEnum.fromCode(type, value);
    }
}
