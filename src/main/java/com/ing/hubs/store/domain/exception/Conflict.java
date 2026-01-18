package com.ing.hubs.store.domain.exception;

public class Conflict extends RuntimeException {
    public Conflict(String message) {
        super(message);
    }
}
