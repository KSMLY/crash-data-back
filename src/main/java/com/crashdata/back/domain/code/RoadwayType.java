package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum RoadwayType implements CodedEnum {

    MOTORWAY(1),
    EXPRESS_ROAD(2),
    URBAN_TWO_WAY(3),
    URBAN_ONE_WAY(4),
    OUTSIDE_BUILT_UP_AREA(5),
    RESTRICTED_ROAD(6),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    RoadwayType(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<RoadwayType> {
        public Conv() {
            super(RoadwayType.class);
        }
    }
}
