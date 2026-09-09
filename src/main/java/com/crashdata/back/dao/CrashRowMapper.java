package com.crashdata.back.dao;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Municipality;
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
        Long districtId = rs.getLong("district_id");
        District district = new District(
                districtId,
                rs.getLong("governorate_id"),
                rs.getString("district_name_en"),
                rs.getString("district_name_ar"));

        Long municipalityId = rs.getObject("municipality_id", Long.class);
        Municipality municipality = municipalityId == null ? null : new Municipality(
                municipalityId,
                districtId,
                rs.getString("municipality_name_en"),
                rs.getString("municipality_name_ar"));
        return new Crash(
                id,
                rs.getString("police_ref"),
                rs.getShort("ref_year"),
                rs.getObject("crash_date", LocalDate.class),
                rs.getObject("crash_time", LocalTime.class),
                district,
                municipality,
                rs.getBigDecimal("latitude"),
                rs.getBigDecimal("longitude"),
                Codes.of(rs, "crash_type_code", CrashType.class),
                Codes.of(rs, "impact_type_code", ImpactType.class),
                Codes.of(rs, "weather_code", Weather.class),
                Codes.of(rs, "light_code", Light.class),
                Codes.of(rs, "severity_code", CrashSeverity.class),
                Codes.of(rs, "roadway_type_code", RoadwayType.class),
                Codes.of(rs, "functional_class_code", FunctionalClass.class),
                rs.getShort("speed_limit_kmh"),
                Codes.of(rs, "obstacle_present_code", ObstaclePresent.class),
                Codes.of(rs, "surface_condition_code", SurfaceCondition.class),
                Codes.of(rs, "junction_type_code", JunctionType.class),
                Codes.of(rs, "curve_code", Curve.class),
                Codes.of(rs, "grade_code", Grade.class),
                controlsByCrashId.getOrDefault(id, Set.of()));
    }
}
