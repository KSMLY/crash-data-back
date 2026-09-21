package com.crashdata.back.dto;

import java.time.LocalDate;

public record TrendPointDto(LocalDate periodStart, long total, long fatal, long serious, long slight) {
}
