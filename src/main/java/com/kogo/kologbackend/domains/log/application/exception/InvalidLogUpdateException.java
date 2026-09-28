package com.kogo.kologbackend.domains.log.application.exception;

public class InvalidLogUpdateException extends RuntimeException {
    public InvalidLogUpdateException(String message) {
        super(message);
    }
}
