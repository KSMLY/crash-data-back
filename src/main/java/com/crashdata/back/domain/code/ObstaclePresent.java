package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum ObstaclePresent implements CodedEnum {

    YES(1),
    NO(2),
    UNKNOWN(9);

    private final short code;

    ObstaclePresent(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<ObstaclePresent> {
        public Conv() {
            super(ObstaclePresent.class);
        }
    }
}
