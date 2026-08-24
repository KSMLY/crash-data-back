package com.crashdata.back.code;

public enum TestType implements CodedEnum {

    BLOOD(1),
    BREATH(2),
    URINE(3),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    TestType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
