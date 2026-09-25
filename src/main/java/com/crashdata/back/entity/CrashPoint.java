package com.crashdata.back.entity;

import com.crashdata.back.code.CrashSeverity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CrashPoint(
        Long id,
        String policeRef,
        LocalDate crashDate,
        BigDecimal latitude,
        BigDecimal longitude,
        CrashSeverity severity
) {
}
