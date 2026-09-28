package com.kogo.kologbackend.domains.log.application.exception;

public class InvalidLogQueryException extends RuntimeException {
    public InvalidLogQueryException(String message) {
        super(message);
    }
}
