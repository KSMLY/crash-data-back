package com.crashdata.back.controller;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrashCsvTest {

    @Test
    void fieldWritesPlainValuesAsIs() {
        assertEquals("Baabda", CrashCsv.field("Baabda"));
        assertEquals("33.5", CrashCsv.field(new BigDecimal("33.5")));
    }

    @Test
    void fieldWritesNullAsEmpty() {
        assertEquals("", CrashCsv.field(null));
    }

    @Test
    void fieldQuotesDelimitersAndLineBreaks() {
        assertEquals("\"Jdeideh, Matn\"", CrashCsv.field("Jdeideh, Matn"));
        assertEquals("\"line\nbreak\"", CrashCsv.field("line\nbreak"));
    }

    @Test
    void fieldDoublesQuotesInsideAQuotedValue() {
        assertEquals("\"the \"\"old\"\" road\"", CrashCsv.field("the \"old\" road"));
    }
}
