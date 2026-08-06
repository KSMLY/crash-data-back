package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum PedManoeuvre implements CodedEnum {

    CROSSING(1),
    WALKING_ON_CARRIAGEWAY(2),
    STANDING_ON_CARRIAGEWAY(3),
    NOT_ON_CARRIAGEWAY(4),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    PedManoeuvre(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<PedManoeuvre> {
        public Conv() {
            super(PedManoeuvre.class);
        }
    }
}
