package com.crashdata.back.entity;

import com.crashdata.back.code.CrashSeverity;

import java.math.BigDecimal;

public record CrashPoint(
        Long id,
        BigDecimal latitude,
        BigDecimal longitude,
        CrashSeverity severity
) {
}
