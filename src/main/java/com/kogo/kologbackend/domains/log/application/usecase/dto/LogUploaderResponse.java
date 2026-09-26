package com.kogo.kologbackend.domains.log.application.usecase.dto;

import lombok.Builder;

@Builder
public record LogUploaderResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {}
