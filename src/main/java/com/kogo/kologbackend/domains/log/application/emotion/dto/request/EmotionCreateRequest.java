package com.kogo.kologbackend.domains.log.application.emotion.dto.request;

public record EmotionCreateRequest(
        Long logId,
        String emotionId
) {
}
