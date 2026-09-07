package com.crashdata.back.entity;

import com.crashdata.back.code.ResultStatus;
import com.crashdata.back.code.TestStatus;
import com.crashdata.back.code.TestType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AlcoholTest {
    private Long personId;
    private TestStatus testStatus;
    private TestType testType;
    private ResultStatus resultStatus;
    private BigDecimal resultValue;

    public AlcoholTest withPersonId(Long personId) {
        return new AlcoholTest(personId, testStatus, testType, resultStatus, resultValue);
    }
}
