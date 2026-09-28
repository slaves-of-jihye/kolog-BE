package com.kogo.kologbackend.domains.emotion.application.usecase.crud.dto.response;

import com.kogo.kologbackend.domains.emotion.domain.Emotion;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.UserResponse;
import lombok.Builder;

@Builder
public record EmotionResponse(
        Long id,
        UserResponse author,
        String content
) {
    public static EmotionResponse from(Emotion emotion) {
        return EmotionResponse.builder()
                .id(emotion.id())
                .author(UserResponse.from(emotion.author()))
                .content(emotion.content())
                .build();
    }
}
