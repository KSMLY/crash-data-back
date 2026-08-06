package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum Grade implements CodedEnum {

    YES(1),
    NO(2),
    UNKNOWN(9);

    private final short code;

    Grade(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<Grade> {
        public Conv() {
            super(Grade.class);
        }
    }
}
