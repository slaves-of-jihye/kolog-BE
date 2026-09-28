package com.kogo.kologbackend.domains.user.application.external.dto;

import lombok.Builder;

@Builder
public record UserDetail(
        Long userId
) {}
