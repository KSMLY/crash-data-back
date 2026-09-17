package com.crashdata.back.controller;

import com.crashdata.back.service.InvalidCrashException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCrashException.class)
    public ResponseEntity<String> handleInvalidCrash(InvalidCrashException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Only the parameter name goes back to the client; the cause is a NumberFormatException
    // whose message would otherwise leak the raw JDK text
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(e.getName() + " must be a number.");
    }

    // Also raised for query parameters bound to a record (GET /crashes). A value that
    // cannot be converted at all, like severity=BOGUS, arrives as a binding failure whose
    // default message is the raw converter text, so it is rewritten here
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidRequest(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + (error.isBindingFailure()
                        ? conversionMessage(e.getParameter().getParameterType(), error)
                        : error.getDefaultMessage()))
                .sorted()
                .collect(Collectors.joining(", ")));
    }

    private static String conversionMessage(Class<?> target, FieldError error) {
        if (target.isRecord()) {
            for (RecordComponent component : target.getRecordComponents()) {
                if (component.getName().equals(error.getField()) && component.getType().isEnum()) {
                    return "must be one of: " + allowed(component.getType());
                }
            }
        }
        return "is not valid.";
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