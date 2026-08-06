package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum CrashSeverity implements CodedEnum {

    FATAL(1),
    SERIOUS(2),
    SLIGHT(3);

    private final short code;

    CrashSeverity(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<CrashSeverity> {
        public Conv() {
            super(CrashSeverity.class);
        }
    }
}
