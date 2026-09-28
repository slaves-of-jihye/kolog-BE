package com.kogo.kologbackend.domains.emotion.application.usecase.crud.dto.request;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

@Builder
public record EmotionCreateRequest(
        Long logId,
        UserDetail requester,
        String content
) {}
