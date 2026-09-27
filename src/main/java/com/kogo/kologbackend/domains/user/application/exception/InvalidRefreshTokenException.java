package com.kogo.kologbackend.domains.user.application.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("리프레시 토큰이 유효하지 않습니다.");
    }
}
