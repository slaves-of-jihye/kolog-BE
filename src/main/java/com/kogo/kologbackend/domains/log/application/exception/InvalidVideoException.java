package com.kogo.kologbackend.domains.log.application.exception;

public class InvalidVideoException extends RuntimeException {
    public InvalidVideoException(String message) {
        super(message);
    }
}
