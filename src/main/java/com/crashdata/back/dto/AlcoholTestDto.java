package com.crashdata.back.dto;

import com.crashdata.back.code.ResultStatus;
import com.crashdata.back.code.TestStatus;
import com.crashdata.back.code.TestType;

import java.math.BigDecimal;

public record AlcoholTestDto(
        TestStatus testStatus,
        TestType testType,
        ResultStatus resultStatus,
        BigDecimal resultValue) {
}
