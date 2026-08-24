package com.crashdata.back.code;


public enum CrashSeverity implements CodedEnum {

    FATAL(1),
    SERIOUS(2),
    SLIGHT(3);

    private final short code;

    CrashSeverity(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
