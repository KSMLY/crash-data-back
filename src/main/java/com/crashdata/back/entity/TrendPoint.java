package com.crashdata.back.entity;

import java.time.LocalDate;

/** One bucket of the trend series; periodStart is the first day of the bucket. */
public record TrendPoint(LocalDate periodStart, long total, long fatal, long serious, long slight) {

    public static TrendPoint empty(LocalDate periodStart) {
        return new TrendPoint(periodStart, 0, 0, 0, 0);
    }
}
