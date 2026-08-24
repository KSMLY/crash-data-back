package com.crashdata.back.code;

public enum AlcoholSuspected implements CodedEnum {

    NO(1),
    YES(2),
    NOT_APPLICABLE(3),
    UNKNOWN(9);

    private final short code;

    AlcoholSuspected(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
