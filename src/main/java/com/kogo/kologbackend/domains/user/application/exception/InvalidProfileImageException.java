package com.kogo.kologbackend.domains.user.application.exception;

public class InvalidProfileImageException extends RuntimeException {
    public InvalidProfileImageException(String message) {
        super(message);
    }
}
