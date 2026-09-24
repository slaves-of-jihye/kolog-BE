package com.kogo.kologbackend.domains.user.application.usecase.auth.dto;

public record UserLoginRequest(
        String email,
        String password
) {}
