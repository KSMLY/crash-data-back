package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum RoadUserType implements CodedEnum {

    DRIVER(1),
    PASSENGER(2),
    PEDESTRIAN(3),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    RoadUserType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<RoadUserType> {
        public Conv() {
            super(RoadUserType.class);
        }
    }
}
