package com.crashdata.back.entity;

import java.util.List;
import java.util.Map;

/**
 * One crash with all details related to it. The alcohol tests are keyed by
 * person id, since a test belongs to a person rather than to the crash.
 */
public record CrashDetail(
        Crash crash,
        List<Vehicle> vehicles,
        List<Person> persons,
        Map<Long, AlcoholTest> testsByPersonId) {
}
