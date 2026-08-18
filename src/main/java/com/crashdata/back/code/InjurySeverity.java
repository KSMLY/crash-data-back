package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum InjurySeverity implements CodedEnum {

    FATAL(1),
    SERIOUS(2),
    SLIGHT(3),
    NO_INJURY(4),
    UNKNOWN(9);

    private final short code;

    InjurySeverity(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<InjurySeverity> {
        public Conv() {
            super(InjurySeverity.class);
        }
    }
}
