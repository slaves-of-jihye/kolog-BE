package com.kogo.kologbackend.domains.user.application.usecase.me.dto;

import lombok.Builder;

@Builder
public record UserMeResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {}
