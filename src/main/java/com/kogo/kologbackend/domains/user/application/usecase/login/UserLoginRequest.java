package com.kogo.kologbackend.domains.user.application.usecase.login;

public record UserLoginRequest(
        String email,
        String password
) {}
