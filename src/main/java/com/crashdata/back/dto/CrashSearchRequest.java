package com.crashdata.back.dto;

import com.crashdata.back.code.CrashSeverity;
import com.crashdata.back.code.CrashType;
import com.crashdata.back.entity.CrashSearch;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * Query parameters of GET /crashes. Every field is optional; the defaults are applied
 * in {@link #toSearch()} rather than on the fields so a missing parameter and an
 * explicit one validate the same way.
 */
public record CrashSearchRequest(
        String q,
        CrashSeverity severity,
        CrashType crashType,
        Long districtId,
        LocalDate from,
        LocalDate to,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size,
        @Pattern(regexp = "crashDate|severity|policeRef") String sort,
        @Pattern(regexp = "asc|desc") String order) {

    public static final int DEFAULT_SIZE = 20;

    public CrashSearch toSearch() {
        String sortKey = sort == null ? "crashDate" : sort;
        // Newest first is the natural default for dates; A-Z for everything else
        boolean descending = order == null ? sortKey.equals("crashDate") : order.equals("desc");
        return new CrashSearch(
                q == null || q.isBlank() ? null : q.trim(),
                severity, crashType, districtId, from, to,
                page == null ? 0 : page,
                size == null ? DEFAULT_SIZE : size,
                sortKey,
                descending);
    }
}
