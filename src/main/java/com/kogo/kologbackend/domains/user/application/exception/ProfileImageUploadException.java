package com.kogo.kologbackend.domains.user.application.exception;

public class ProfileImageUploadException extends RuntimeException {
    public ProfileImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
