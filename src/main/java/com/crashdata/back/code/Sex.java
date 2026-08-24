package com.crashdata.back.code;

public enum Sex implements CodedEnum {

    MALE(1),
    FEMALE(2),
    UNKNOWN(9);

    private final short code;

    Sex(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
