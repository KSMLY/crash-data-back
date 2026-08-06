package com.crashdata.back.domain.code;

import jakarta.persistence.AttributeConverter;

public abstract class CodedEnumConverter<E extends Enum<E> & CodedEnum>
        implements AttributeConverter<E, Short> {

    private final Class<E> type;

    protected CodedEnumConverter(Class<E> type) {
        this.type = type;
    }

    @Override
    public Short convertToDatabaseColumn(E value) {
        return value == null ? null : value.getCode();
    }

    @Override
    public E convertToEntityAttribute(Short code) {
        if (code == null) return null;
        for (E e : type.getEnumConstants()) {
            if (e.getCode() == code) return e;
        }
        throw new IllegalArgumentException(
                "Unknown " + type.getSimpleName() + " code: " + code);
    }
}