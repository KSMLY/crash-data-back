package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum VehicleType implements CodedEnum {

    BICYCLE(1),
    OTHER_NON_MOTOR_VEHICLE(2),
    TWO_THREE_WHEEL_MOTOR_VEHICLE(3),
    PASSENGER_CAR(4),
    BUS(5),
    LIGHT_GOODS_VEHICLE(6),
    HEAVY_GOODS_VEHICLE(7),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    VehicleType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<VehicleType> {
        public Conv() {
            super(VehicleType.class);
        }
    }
}
