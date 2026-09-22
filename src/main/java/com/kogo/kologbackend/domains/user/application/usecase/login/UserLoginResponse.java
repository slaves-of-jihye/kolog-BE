package com.kogo.kologbackend.domains.user.application.usecase.login;

public record UserLoginResponse(
        String grantType,
        String accessToken,
        String refreshToken
) {}
