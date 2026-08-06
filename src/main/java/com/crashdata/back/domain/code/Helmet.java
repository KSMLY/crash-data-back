package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum Helmet implements CodedEnum {

    WORN(1),
    NOT_WORN(2),
    NOT_APPLICABLE(3),
    UNKNOWN(9);

    private final short code;

    Helmet(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<Helmet> {
        public Conv() {
            super(Helmet.class);
        }
    }
}
