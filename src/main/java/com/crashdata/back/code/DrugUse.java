package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum DrugUse implements CodedEnum {

    NO_SUSPICION(1),
    SUSPICION(2),
    EVIDENCE(3),
    NOT_APPLICABLE(4),
    UNKNOWN(9);

    private final short code;

    DrugUse(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<DrugUse> {
        public Conv() {
            super(DrugUse.class);
        }
    }
}
