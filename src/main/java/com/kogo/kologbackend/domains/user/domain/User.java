package com.kogo.kologbackend.domains.user.domain;

public record User(
        Long id,
        String email,
        String password,
        String nickname,
        String profileImageUrl
) {}
