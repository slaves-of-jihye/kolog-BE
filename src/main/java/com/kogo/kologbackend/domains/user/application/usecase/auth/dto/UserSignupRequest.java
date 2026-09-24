package com.kogo.kologbackend.domains.user.application.usecase.auth.dto;

public record UserSignupRequest(
        String email,
        String password,
        String nickname
) {}