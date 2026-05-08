package com.kogo.kologbackend.application.emotion.internal;

import com.kogo.kologbackend.application.emotion.dto.request.EmotionCreateRequest;

public interface EmotionCreateUseCase {
    void createEmotion(EmotionCreateRequest emotionCreateRequest);
}
