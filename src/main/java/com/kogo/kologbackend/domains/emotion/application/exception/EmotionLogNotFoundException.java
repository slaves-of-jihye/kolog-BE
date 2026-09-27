package com.kogo.kologbackend.domains.emotion.application.exception;

public class EmotionLogNotFoundException extends RuntimeException {
    public EmotionLogNotFoundException() {
        super("로그를 찾을 수 없습니다.");
    }
}
