package com.kogo.kologbackend.domains.log.application.exception;

public class LogUpdateForbiddenException extends RuntimeException {
    public LogUpdateForbiddenException() {
        super("본인이 작성한 로그만 수정할 수 있습니다.");
    }
}
