package com.crashdata.back.dto;

import com.crashdata.back.code.CrashSeverity;

import java.math.BigDecimal;

public record CrashPointDto(
        long id,
        BigDecimal latitude,
        BigDecimal longitude,
        CrashSeverity severity) {
}
