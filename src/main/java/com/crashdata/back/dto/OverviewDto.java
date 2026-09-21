package com.crashdata.back.dto;

import java.time.LocalDate;
import java.util.List;

public record OverviewDto(
        LocalDate from, LocalDate to,
        LocalDate previousFrom, LocalDate previousTo,
        CountsDto total, CountsDto fatal, CountsDto serious,
        List<DistrictCountsDto> topDistricts) {
}
