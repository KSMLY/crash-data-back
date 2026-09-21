package com.crashdata.back.entity;

import java.time.LocalDate;
import java.util.List;

/** The overview page's numbers: a window, the window before it, and the busiest districts. */
public record Overview(
        LocalDate from, LocalDate to,
        LocalDate previousFrom, LocalDate previousTo,
        SeverityCounts counts,
        List<DistrictCounts> topDistricts) {
}
