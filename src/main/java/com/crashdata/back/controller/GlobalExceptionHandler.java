package com.crashdata.back.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadCode(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidRequest(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", ")));
    }

    // Jackson rejects an unknown enum name before the body ever reaches the controller,
    // so this is where a bad code value is reported rather than in a validator
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleUnreadableBody(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException invalid && invalid.getTargetType().isEnum()) {
            return ResponseEntity.badRequest()
                    .body(path(invalid) + " must be one of: " + allowed(invalid.getTargetType()));
        }
        return ResponseEntity.badRequest().body("Request body is not readable JSON.");
    }

    // Rebuilds the dotted path Jackson walked, so a nested failure reads persons[0].sex
    private static String path(InvalidFormatException e) {
        StringBuilder path = new StringBuilder();
        for (JacksonException.Reference reference : e.getPath()) {
            if (reference.getPropertyName() != null) {
                if (!path.isEmpty()) path.append('.');
                path.append(reference.getPropertyName());
            } else {
                path.append('[').append(reference.getIndex()).append(']');
            }
        }
        return path.toString();
    }

    private static String allowed(Class<?> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDupeKey(DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("A crash with this police_ref already exists for that year.");
    }
}