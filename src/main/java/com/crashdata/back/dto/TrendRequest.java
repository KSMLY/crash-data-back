package com.crashdata.back.dto;

import com.crashdata.back.entity.Granularity;

import java.time.LocalDate;

/**
 * Query parameters of GET /overview/trend. Bound as a record so an unknown granularity
 * gets the "must be one of" message from the exception handler.
 */
public record TrendRequest(Granularity granularity, LocalDate to) {

    public Granularity granularityOrDaily() {
        return granularity == null ? Granularity.DAILY : granularity;
    }

    public LocalDate toOrToday() {
        return to == null ? LocalDate.now() : to;
    }
}
