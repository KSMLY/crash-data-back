package com.crashdata.back.code;

public enum PedManoeuvre implements CodedEnum {

    CROSSING(1),
    WALKING_ON_CARRIAGEWAY(2),
    STANDING_ON_CARRIAGEWAY(3),
    NOT_ON_CARRIAGEWAY(4),
    OTHER(8),
    UNKNOWN(9);

    private final short code;

    PedManoeuvre(int code) {
        this.code = (short) code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
