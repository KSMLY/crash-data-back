package com.crashdata.back.dao;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Reads a statement from src/main/resources/sql. Called from static initialisers, so a
 * missing or unreadable file fails while the DAO class loads rather than on first query.
 */
final class Sql {

    private Sql() {
    }

    static String load(String path) {
        try {
            return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read SQL file " + path, e);
        }
    }
}
