package com.crashdata.back.code;

public enum Weather implements CodedEnum {

    CLEAR(1),
    RAIN(2),
    SNOW(3),
    FOG(4),
    SLEET(5),
    SEVERE_WINDS(6),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    Weather(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}