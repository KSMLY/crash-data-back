package com.crashdata.back.entity;

/** Crash counts by severity for a window and for the window before it. */
public record SeverityCounts(
        long total, long fatal, long serious,
        long previousTotal, long previousFatal, long previousSerious) {
}
