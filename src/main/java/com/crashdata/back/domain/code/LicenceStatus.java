package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

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

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<LicenceStatus> {
        public Conv() {
            super(LicenceStatus.class);
        }
    }
}
