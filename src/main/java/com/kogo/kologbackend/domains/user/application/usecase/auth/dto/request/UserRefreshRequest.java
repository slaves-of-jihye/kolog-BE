package com.kogo.kologbackend.domains.user.application.usecase.auth.dto.request;

import lombok.Builder;

@Builder
public record UserRefreshRequest(
        String refreshToken
) {}
