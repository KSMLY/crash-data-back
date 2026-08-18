package com.crashdata.back.code;

import jakarta.persistence.Converter;

// No other/unknown value in this list, unlike most of the code lists.
public enum FunctionalClass implements CodedEnum {

    PRINCIPAL_ARTERIAL(1),
    SECONDARY_ARTERIAL(2),
    COLLECTOR(3),
    LOCAL(4);

    private final short code;

    FunctionalClass(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<FunctionalClass> {
        public Conv() {
            super(FunctionalClass.class);
        }
    }
}
