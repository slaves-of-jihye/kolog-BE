package com.kogo.kologbackend.domains.log.application.emotion.internal;

import com.kogo.kologbackend.domains.log.application.emotion.dto.request.EmotionCreateRequest;

public interface EmotionCreateUseCase {
    void createEmotion(Long userId, EmotionCreateRequest emotionCreateRequest);
}
