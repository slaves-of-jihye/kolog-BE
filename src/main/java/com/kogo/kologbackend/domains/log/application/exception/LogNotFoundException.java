package com.kogo.kologbackend.domains.log.application.exception;

public class LogNotFoundException extends RuntimeException {
    public LogNotFoundException() {
        super("로그를 찾을 수 없습니다.");
    }
}
