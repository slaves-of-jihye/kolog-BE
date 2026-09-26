package com.kogo.kologbackend.domains.log.application.exception;

public class LogUserNotFoundException extends RuntimeException {
    public LogUserNotFoundException() {
        super("유저를 찾을 수 없습니다.");
    }
}
