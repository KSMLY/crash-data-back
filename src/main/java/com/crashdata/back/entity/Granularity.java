package com.crashdata.back.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Bucket size of the trend series, with the DATETRUNC unit it maps to. */
@Getter
@AllArgsConstructor
public enum Granularity {
    DAILY("day"),
    WEEKLY("iso_week"),   // Monday-based whatever the session's DATEFIRST is
    MONTHLY("month");

    private final String sqlUnit;
}
