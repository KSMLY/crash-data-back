package com.crashdata.back.entity;

public record DistrictCounts(
        District district,
        long total, long fatal, long serious,
        long previousTotal) {
}
