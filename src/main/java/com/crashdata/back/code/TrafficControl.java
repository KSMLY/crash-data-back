package com.crashdata.back.code;

import jakarta.persistence.Converter;

// No unknown value in this list, unlike most of the code lists.
public enum TrafficControl implements CodedEnum {

    AUTHORISED_PERSON(1),
    STOP_SIGN(2),
    GIVE_WAY(3),
    OTHER_TRAFFIC_SIGNS(4),
    SIGNAL_WORKING(5),
    SIGNAL_OUT_OF_ORDER(6),
    UNCONTROLLED(7),
    OTHER(8);

    private final short code;

    TrafficControl(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<TrafficControl> {
        public Conv() {
            super(TrafficControl.class);
        }
    }
}
