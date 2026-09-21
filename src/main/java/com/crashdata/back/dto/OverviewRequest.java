package com.crashdata.back.dto;

import java.time.LocalDate;

/** Query parameters of GET /overview. Bound as a record so a bad date reads "to is not valid." */
public record OverviewRequest(LocalDate to) {

    public LocalDate toOrToday() {
        return to == null ? LocalDate.now() : to;
    }
}
