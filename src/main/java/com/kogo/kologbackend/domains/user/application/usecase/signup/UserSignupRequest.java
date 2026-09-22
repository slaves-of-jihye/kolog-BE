package com.kogo.kologbackend.domains.user.application.usecase.signup;

public record UserSignupRequest(
        String email,
        String password,
        String nickname
) {}