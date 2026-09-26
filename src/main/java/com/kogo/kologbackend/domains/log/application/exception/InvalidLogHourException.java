package com.kogo.kologbackend.domains.log.application.exception;

public class InvalidLogHourException extends RuntimeException {
    public InvalidLogHourException() {
        super("Hour must be between 0 and 23.");
    }
}
