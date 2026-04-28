package com.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Triggered when @Valid fails on a @RequestBody — returns 400 with field-level messages
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Map.of(
                "status",    400,
                "error",     "Validation Failed",
                "message",   errors,
                "timestamp", Instant.now().toString()
        );
    }

    // Triggered by service-layer business rule violations (e.g. invalid price range)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadInput(IllegalArgumentException ex) {
        return Map.of(
                "status",    400,
                "error",     "Bad Request",
                "message",   ex.getMessage(),
                "timestamp", Instant.now().toString()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ResourceNotFoundException ex) {
        return Map.of(
                "status",    404,
                "error",     "Not Found",
                "message",   ex.getMessage(),
                "timestamp", Instant.now().toString()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneric(Exception ex) {
        return Map.of(
                "status",    500,
                "error",     "Internal Server Error",
                "message",   ex.getMessage(),
                "timestamp", Instant.now().toString()
        );
    }
}
