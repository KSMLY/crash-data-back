package com.crashdata.back.entity;

import com.crashdata.back.code.CrashSeverity;
import com.crashdata.back.code.CrashType;

import java.time.LocalDate;

public record CrashSearch(
        String q,
        CrashSeverity severity,
        CrashType crashType,
        Long districtId,
        Long municipalityId,
        LocalDate from, LocalDate to,
        int page, int size,
        String sort, boolean descending) {
}
