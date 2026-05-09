package com.kogo.kologbackend.adapter.auth.dto.response;

public record LoginResponse(
        String grantType,
        String accessToken,
        String refreshToken
) {}
