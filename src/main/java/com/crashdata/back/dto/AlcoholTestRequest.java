package com.crashdata.back.dto;

import com.crashdata.back.code.ResultStatus;
import com.crashdata.back.code.TestStatus;
import com.crashdata.back.code.TestType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlcoholTestRequest(
        @NotNull TestStatus testStatus,
        @NotNull TestType testType,
        @NotNull ResultStatus resultStatus,
        BigDecimal resultValue) {
}
