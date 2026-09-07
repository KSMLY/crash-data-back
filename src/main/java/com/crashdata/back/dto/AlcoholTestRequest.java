package com.crashdata.back.dto;

import java.math.BigDecimal;

public record AlcoholTestRequest(
        Short testStatusCode,
        Short testTypeCode,
        Short resultStatusCode,
        BigDecimal resultValue) {
}
