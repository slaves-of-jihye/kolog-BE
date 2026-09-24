package com.kogo.kologbackend.domains.user.application.usecase.auth.dto;

public record UserAuthResponse(
        String accessToken,
        String refreshToken
) {
}