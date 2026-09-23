package com.kogo.kologbackend.domains.log.application.usecase.emotionCreate;

public record EmotionCreateRequest(
        Long logId,
        String emotionId
) {
}
