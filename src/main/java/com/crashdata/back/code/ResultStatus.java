package com.crashdata.back.code;

// Local codes, not WHO's - the spec gives no numbers for these values.
public enum ResultStatus implements CodedEnum {

    AVAILABLE(1),
    PENDING(2),
    UNKNOWN(9);

    private final short code;

    ResultStatus(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
