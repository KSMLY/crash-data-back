package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum TestType implements CodedEnum {

    BLOOD(1),
    BREATH(2),
    URINE(3),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    TestType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<TestType> {
        public Conv() {
            super(TestType.class);
        }
    }
}
