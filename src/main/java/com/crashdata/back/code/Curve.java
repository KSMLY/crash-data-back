package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum Curve implements CodedEnum {

    TIGHT(1),
    OPEN(2),
    NONE(3),
    UNKNOWN(9);

    private final short code;

    Curve(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<Curve> {
        public Conv() {
            super(Curve.class);
        }
    }
}
