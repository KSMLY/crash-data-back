package com.crashdata.back.dto;

import com.crashdata.back.code.CrashSeverity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CrashPointDto(
        long id,
        String policeRef,
        LocalDate crashDate,
        BigDecimal latitude,
        BigDecimal longitude,
        CrashSeverity severity) {
}
