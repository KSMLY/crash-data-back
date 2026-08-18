package com.crashdata.back.code;

import jakarta.persistence.Converter;

public enum Light implements CodedEnum {

    DAYLIGHT(1),
    TWILIGHT(2),
    DARKNESS_NO_LIGHTING(3),
    DARK_STREET_LIGHTS_UNLIT(4),
    DARK_STREET_LIGHTS_LIT(5),
    UNKNOWN(9);

    private final short code;

    Light(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<Light> {
        public Conv() {
            super(Light.class);
        }
    }
}
