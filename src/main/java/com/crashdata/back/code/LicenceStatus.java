package com.crashdata.back.code;

// Local codes, not WHO's - the spec gives no numbers for these values.
public enum LicenceStatus implements CodedEnum {

    ISSUED(1),
    NEVER_ISSUED(2),
    UNKNOWN(9);

    private final short code;

    LicenceStatus(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
