package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum SeatPosition implements CodedEnum {

    LEFT(1),
    MIDDLE(2),
    RIGHT(3),
    NOT_APPLICABLE(4),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    SeatPosition(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<SeatPosition> {
        public Conv() {
            super(SeatPosition.class);
        }
    }
}
