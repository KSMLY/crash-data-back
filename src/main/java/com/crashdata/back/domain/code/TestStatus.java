package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum TestStatus implements CodedEnum {

    NOT_GIVEN(1),
    REFUSED(2),
    GIVEN(3),
    UNKNOWN(9);

    private final short code;

    TestStatus(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<TestStatus> {
        public Conv() {
            super(TestStatus.class);
        }
    }
}
