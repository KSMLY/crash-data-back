package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum AlcoholSuspected implements CodedEnum {

    NO(1),
    YES(2),
    NOT_APPLICABLE(3),
    UNKNOWN(9);

    private final short code;

    AlcoholSuspected(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<AlcoholSuspected> {
        public Conv() {
            super(AlcoholSuspected.class);
        }
    }
}
