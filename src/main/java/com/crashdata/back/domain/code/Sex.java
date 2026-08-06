package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum Sex implements CodedEnum {

    MALE(1),
    FEMALE(2),
    UNKNOWN(9);

    private final short code;

    Sex(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<Sex> {
        public Conv() {
            super(Sex.class);
        }
    }
}
