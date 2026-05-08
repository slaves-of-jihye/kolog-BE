package com.kogo.kologbackend.application.emotion.dto.request;

public record EmotionCreateRequest(
        Long logId,
        Long emotionId
) {
}
