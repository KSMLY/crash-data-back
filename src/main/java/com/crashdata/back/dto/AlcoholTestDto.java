package com.crashdata.back.dto;

import java.math.BigDecimal;

public record AlcoholTestDto(
        Short testStatusCode,
        Short testTypeCode,
        Short resultStatusCode,
        BigDecimal resultValue) {
}
