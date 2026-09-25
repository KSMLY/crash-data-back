package com.crashdata.back.controller;

import com.crashdata.back.entity.Crash;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * One CSV line per crash for GET /crashes/export. Lines end in CRLF as RFC 4180 asks,
 * and enums are written by name, the same way the JSON endpoints return them.
 */
final class CrashCsv {

    static final String HEADER = line(Stream.of(
            "police_ref", "crash_date", "crash_time", "district", "municipality",
            "severity", "crash_type", "latitude", "longitude"));

    private CrashCsv() {
    }

    static String row(Crash crash) {
        return line(Stream.of(
                crash.getPoliceRef(),
                crash.getCrashDate(),
                crash.getCrashTime(),
                crash.getDistrict().getNameEn(),
                crash.getMunicipality() == null ? null : crash.getMunicipality().getNameEn(),
                crash.getSeverity(),
                crash.getCrashType(),
                crash.getLatitude(),
                crash.getLongitude()));
    }

    private static String line(Stream<?> values) {
        return values.map(CrashCsv::field).collect(Collectors.joining(",")) + "\r\n";
    }

    // A value holding a delimiter, quote or line break is wrapped in quotes with its own
    // quotes doubled; everything else is written as is
    static String field(Object value) {
        if (value == null) return "";
        String text = value.toString();
        if (text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
