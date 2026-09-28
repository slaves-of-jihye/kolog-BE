package com.kogo.kologbackend.domains.emotion.application.exception;

public class DuplicateEmotionException extends RuntimeException {
    public DuplicateEmotionException() {
        super("이미 해당 로그에 감정표현을 남겼습니다.");
    }
}
