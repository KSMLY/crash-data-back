package com.crashdata.back.dto;

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
            @NotNull Short crashTypeCode,
            @NotNull Short impactTypeCode,
            @NotNull Short weatherCode,
            @NotNull Short lightCode,
            @NotNull Short roadwayTypeCode,
            Short functionalClassCode,
            @NotNull Short speedLimitKmh,
            @NotNull Short obstaclePresentCode,
            @NotNull Short surfaceConditionCode,
            @NotNull Short junctionTypeCode,
            @NotNull Short curveCode,
            @NotNull Short gradeCode,
            List<Short> trafficControlCodes,
            @Valid List<VehicleRequest> vehicles,
            @Valid List<PersonRequest> persons) {
}
