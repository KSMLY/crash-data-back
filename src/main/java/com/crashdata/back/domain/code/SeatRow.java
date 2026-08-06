package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum SeatRow implements CodedEnum {

    FRONT(1),
    REAR(2),
    NOT_APPLICABLE(3),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    SeatRow(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<SeatRow> {
        public Conv() {
            super(SeatRow.class);
        }
    }
}
