package com.crashdata.back.code;

public enum Helmet implements CodedEnum {

    WORN(1),
    NOT_WORN(2),
    NOT_APPLICABLE(3),
    UNKNOWN(9);

    private final short code;

    Helmet(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
