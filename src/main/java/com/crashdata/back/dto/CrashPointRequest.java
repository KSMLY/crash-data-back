package com.crashdata.back.dto;

import java.time.LocalDate;

// Query parameters of GET /crashes/points. Bound as a record so a bad date reads "from is not valid."
public record CrashPointRequest(LocalDate from, LocalDate to) {

    private static final int WINDOW_DAYS = 30;

    public LocalDate toOrToday() {
        return to == null ? LocalDate.now() : to;
    }


    // Defaults to the 30 days ending at {@link #toOrToday()}, like the overview window.
    public LocalDate fromOrDefault() {
        return from == null ? toOrToday().minusDays(WINDOW_DAYS - 1) : from;
    }
}
