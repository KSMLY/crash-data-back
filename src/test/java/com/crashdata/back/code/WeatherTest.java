package com.crashdata.back.code;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeatherTest {

    @Test
    void exposesTheWhoCode() {
        assertEquals((short) 2, Weather.RAIN.getCode());
    }

    @Test
    void readsTheWhoCode() {
        assertEquals(Weather.RAIN, CodedEnum.fromCode(Weather.class, (short) 2));
    }

    @Test
    void badCodeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CodedEnum.fromCode(Weather.class, (short) 7));
    }
}
