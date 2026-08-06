package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum SurfaceCondition implements CodedEnum {

    DRY(1),
    SNOW_FROST_ICE(2),
    SLIPPERY(3),
    WET_DAMP(4),
    FLOOD(5),
    // "Other" is 6 here, not the usual 8.
    OTHER(6),
    UNKNOWN(9);

    private final short code;

    SurfaceCondition(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<SurfaceCondition> {
        public Conv() {
            super(SurfaceCondition.class);
        }
    }
}
