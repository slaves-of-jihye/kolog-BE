package com.kogo.kologbackend.domains.emotion.application.exception;

public class InvalidEmotionException extends RuntimeException {
    public InvalidEmotionException() {
        super("content는 비어 있을 수 없습니다.");
    }
}
