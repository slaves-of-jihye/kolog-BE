package com.kogo.kologbackend.domains.user.application.usecase.crud.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {}
