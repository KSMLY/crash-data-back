package com.crashdata.back.dto;

public record DistrictCountsDto(
        DistrictDto district,
        long total, long fatal, long serious,
        long previousTotal) {
}
