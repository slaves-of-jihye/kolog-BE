package com.kogo.kologbackend.domains.log.application.exception;

public class LogDeleteForbiddenException extends RuntimeException {
    public LogDeleteForbiddenException() {
        super("본인이 작성한 로그만 삭제할 수 있습니다.");
    }
}
