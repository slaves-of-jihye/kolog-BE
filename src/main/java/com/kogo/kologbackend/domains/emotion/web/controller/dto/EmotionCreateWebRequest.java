package com.kogo.kologbackend.domains.emotion.web.controller.dto;

import com.kogo.kologbackend.domains.emotion.application.usecase.crud.dto.request.EmotionCreateRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

@Builder
public record EmotionCreateWebRequest(String content) {
    public EmotionCreateRequest toApplication(Long logId, UserDetail requester) {
        return EmotionCreateRequest.builder()
                .logId(logId)
                .requester(requester)
                .content(content)
                .build();
    }
}
