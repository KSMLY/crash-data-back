package com.crashdata.back.code;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeatherTest {

    private final Weather.Conv conv = new Weather.Conv();

    @Test
    void writesTheWhoCode() {
        assertEquals((short) 2, conv.convertToDatabaseColumn(Weather.RAIN));
    }

    @Test
    void readsTheWhoCode() {
        assertEquals(Weather.RAIN, conv.convertToEntityAttribute((short) 2));
    }

    @Test
    void nullSurvivesBothWays() {
        assertNull(conv.convertToDatabaseColumn(null));
        assertNull(conv.convertToEntityAttribute(null));
    }

    @Test
    void badCodeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> conv.convertToEntityAttribute((short) 7));
    }
}