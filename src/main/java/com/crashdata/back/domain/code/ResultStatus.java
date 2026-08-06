package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

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

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<ResultStatus> {
        public Conv() {
            super(ResultStatus.class);
        }
    }
}
