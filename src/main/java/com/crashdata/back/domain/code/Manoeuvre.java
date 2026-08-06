package com.crashdata.back.domain.code;

import jakarta.persistence.Converter;

public enum Manoeuvre implements CodedEnum {

    REVERSING(1),
    PARKED(2),
    ENTERING_OR_LEAVING_PARKING(3),
    SLOWING_OR_STOPPING(4),
    MOVING_OFF(5),
    WAITING_TO_TURN(6),
    TURNING(7),
    // Spec jumps 7 -> 10, then puts 8/9 at the end instead of in sequence.
    CHANGING_LANE(10),
    AVOIDANCE_MANOEUVRE(11),
    OVERTAKING(12),
    STRAIGHT_FORWARD(13),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    Manoeuvre(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Converter(autoApply = true)
    public static class Conv extends CodedEnumConverter<Manoeuvre> {
        public Conv() {
            super(Manoeuvre.class);
        }
    }
}
