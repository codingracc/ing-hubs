package com.ing.hubs.store.application.controller;

import com.ing.hubs.store.application.dto.ErrorResponse;
import com.ing.hubs.store.domain.exception.BadRequest;
import com.ing.hubs.store.domain.exception.Conflict;
import com.ing.hubs.store.domain.exception.InternalError;
import com.ing.hubs.store.domain.exception.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequest ex) {
        return respond(BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFound ex) {
        return respond(NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(Conflict.class)
    public ResponseEntity<ErrorResponse> handleConflict(Conflict ex) {
        return respond(CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(InternalError.class)
    public ResponseEntity<ErrorResponse> handleInternalError(InternalError ex) {
        return respond(INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        if (message.isBlank()) {
            message = "Validation failed";
        }
        return respond(BAD_REQUEST, message, ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return respond(INTERNAL_SERVER_ERROR, "Unexpected error", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception ex) {
        return respond(INTERNAL_SERVER_ERROR, "Unexpected error", ex);
    }

    private ResponseEntity<ErrorResponse> respond(HttpStatus status, String message, Exception ex) {
        if (status.is4xxClientError()) {
            log.warn("Request failed: {} - {}", status.value(), message);
        } else {
            log.error("Request failed: {} - {}", status.value(), message, ex);
        }
        ErrorResponse body = ErrorResponse.builder()
                .message(message)
                .httpCode(status.value())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
