package com.kogo.kologbackend.domains.log.application.exception;

public class InvalidLogDateException extends RuntimeException {
    public InvalidLogDateException() {
        super("Invalid log date.");
    }
}
