package com.crashdata.back.code;

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
}
