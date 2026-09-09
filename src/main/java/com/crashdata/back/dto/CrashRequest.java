package com.crashdata.back.dto;

import com.crashdata.back.code.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CrashRequest (
            @NotNull String policeRef,
            @NotNull Short refYear,
            LocalDate crashDate,
            LocalTime crashTime,
            @NotNull Long districtId,
            Long municipalityId,
            BigDecimal latitude,
            BigDecimal longitude,
            @NotNull CrashType crashType,
            @NotNull ImpactType impactType,
            @NotNull Weather weather,
            @NotNull Light light,
            @NotNull RoadwayType roadwayType,
            FunctionalClass functionalClass,
            @NotNull Short speedLimitKmh,
            @NotNull ObstaclePresent obstaclePresent,
            @NotNull SurfaceCondition surfaceCondition,
            @NotNull JunctionType junctionType,
            @NotNull Curve curve,
            @NotNull Grade grade,
            List<TrafficControl> trafficControls,
            @Valid List<VehicleRequest> vehicles,
            @Valid List<PersonRequest> persons) {
}
