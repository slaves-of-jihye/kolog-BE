package com.kogo.kologbackend.domains.user.application.usecase.auth.dto;

import lombok.Builder;

@Builder
public record UserLoginRequest(
        String email,
        String password
) {}
