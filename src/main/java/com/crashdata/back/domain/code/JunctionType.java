package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum JunctionType implements CodedEnum {

    AT_GRADE_CROSSROAD(1),
    AT_GRADE_ROUNDABOUT(2),
    AT_GRADE_T_OR_STAGGERED(3),
    AT_GRADE_MULTIPLE(4),
    AT_GRADE_OTHER(5),
    NOT_AT_GRADE(6),
    NOT_AT_JUNCTION(7),
    UNKNOWN(9);

    private final short code;

    JunctionType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<JunctionType> {
        public Conv() {
            super(JunctionType.class);
        }
    }
}
