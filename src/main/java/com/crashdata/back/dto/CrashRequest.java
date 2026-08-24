package com.crashdata.back.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CrashRequest (
            String policeRef,
            Short refYear,
            LocalDate crashDate,
            LocalTime crashTime,
            Long districtId,
            Long municipalityId,
            BigDecimal latitude,
            BigDecimal longitude,
            Short crashTypeCode,
            Short impactTypeCode,
            Short weatherCode,
            Short lightCode,
            Short severityCode,
            Short roadwayTypeCode,
            Short functionalClassCode,
            Short speedLimitKmh,
            Short obstaclePresentCode,
            Short surfaceConditionCode,
            Short junctionTypeCode,
            Short curveCode,
            Short gradeCode,
            List<Short> trafficControlCodes) {
}
