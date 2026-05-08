package com.kogo.kologbackend.application.emotion.dto.request;

public record EmotionCreateRequest(
        Long userId,
        Long logId,
        int emotionId
) {
}
