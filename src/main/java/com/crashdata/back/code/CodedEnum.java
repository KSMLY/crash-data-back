package com.crashdata.back.code;

public interface CodedEnum {
    short getCode();
    static <E extends Enum<E> & CodedEnum> E fromCode(Class<E> type, short code) {
        for (E e : type.getEnumConstants()) {
            if (e.getCode() == code) return e;
        }
        throw new IllegalArgumentException(
                "Unknown " + type.getSimpleName() + " code: " + code);
    }
}