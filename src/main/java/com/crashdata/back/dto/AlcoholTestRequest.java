package com.crashdata.back.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlcoholTestRequest(
        @NotNull Short testStatusCode,
        @NotNull Short testTypeCode,
        @NotNull Short resultStatusCode,
        BigDecimal resultValue) {
}
