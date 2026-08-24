package com.crashdata.back.code;

public enum CrashType implements CodedEnum {

    PEDESTRIAN(1),
    PARKED_VEHICLE(2),
    FIXED_OBSTACLE(3),
    NON_FIXED_OBSTACLE(4),
    ANIMAL(5),
    SINGLE_VEHICLE(6),
    MULTIPLE_VEHICLES(7),
    OTHER(8);

    private final short code;

    CrashType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}