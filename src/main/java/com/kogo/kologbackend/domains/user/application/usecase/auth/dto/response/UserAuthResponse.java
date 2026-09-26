package com.kogo.kologbackend.domains.user.application.usecase.auth.dto.response;

import lombok.Builder;

@Builder
public record UserAuthResponse(
        String accessToken,
        String refreshToken
) {
}