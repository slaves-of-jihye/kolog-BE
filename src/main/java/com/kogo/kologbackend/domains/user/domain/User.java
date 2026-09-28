package com.kogo.kologbackend.domains.user.domain;

import lombok.Builder;

@Builder
public record User(
        Long id,
        String email,
        String password,
        String nickname,
        String profileImageUrl
) {}
