package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum Restraint implements CodedEnum {

    SEATBELT_AVAILABLE_USED(1),
    SEATBELT_AVAILABLE_NOT_USED(2),
    SEATBELT_NOT_AVAILABLE(3),
    CHILD_RESTRAINT_AVAILABLE_USED(4),
    CHILD_RESTRAINT_AVAILABLE_NOT_USED(5),
    CHILD_RESTRAINT_NOT_AVAILABLE(6),
    NOT_APPLICABLE(7),
    OTHER_RESTRAINTS_USED(8),
    UNKNOWN(9),
    // The only list with a real value (10) after unknown (9).
    NO_RESTRAINTS_USED(10);

    private final short code;

    Restraint(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<Restraint> {
        public Conv() {
            super(Restraint.class);
        }
    }
}
