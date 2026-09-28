package com.kogo.kologbackend.domains.emotion.application.exception;

public class EmotionUserNotFoundException extends RuntimeException {
    public EmotionUserNotFoundException() {
        super("유저를 찾을 수 없습니다.");
    }
}
