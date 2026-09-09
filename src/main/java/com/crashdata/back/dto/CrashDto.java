package com.crashdata.back.dto;

import com.crashdata.back.code.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CrashDto(
        Long id,
        String policeRef,
        Short refYear,
        LocalDate crashDate,
        LocalTime crashTime,
        DistrictDto district,
        MunicipalityDto municipality,
        BigDecimal latitude,
        BigDecimal longitude,
        CrashType crashType,
        ImpactType impactType,
        Weather weather,
        Light light,
        CrashSeverity severity,
        RoadwayType roadwayType,
        FunctionalClass functionalClass,
        Short speedLimitKmh,
        ObstaclePresent obstaclePresent,
        SurfaceCondition surfaceCondition,
        JunctionType junctionType,
        Curve curve,
        Grade grade,
        List<TrafficControl> trafficControls) {
}
