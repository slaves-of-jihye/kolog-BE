package com.kogo.kologbackend.domains.user.application.usecase.auth.dto;

import lombok.Builder;

@Builder
public record UserSignupRequest(
        String email,
        String password,
        String nickname
) {}