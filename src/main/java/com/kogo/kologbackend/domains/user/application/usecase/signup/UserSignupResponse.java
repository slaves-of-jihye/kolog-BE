package com.kogo.kologbackend.domains.user.application.usecase.signup;

public record UserSignupResponse(
        String accessToken,
        String refreshToken
) {
}