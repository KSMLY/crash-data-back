package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum SpecialFunction implements CodedEnum {

    NONE(1),
    TAXI(2),
    BUS_USE(3),
    POLICE_OR_MILITARY(4),
    EMERGENCY_VEHICLE(5),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    SpecialFunction(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<SpecialFunction> {
        public Conv() {
            super(SpecialFunction.class);
        }
    }
}
