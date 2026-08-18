package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum ImpactType implements CodedEnum {

    NO_IMPACT(1),
    REAR_END(2),
    HEAD_ON(3),
    ANGLE_SAME_DIRECTION(4),
    ANGLE_OPPOSITE_DIRECTION(5),
    ANGLE_RIGHT_ANGLE(6),
    ANGLE_UNSPECIFIED(7),
    SIDE_BY_SIDE_SAME_DIRECTION(8),
    SIDE_BY_SIDE_OPPOSITE_DIRECTION(9),
    REAR_TO_SIDE(10),
    REAR_TO_REAR(11);

    private final short code;

    ImpactType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<ImpactType> {
        public Conv() {
            super(ImpactType.class);
        }
    }
}
