package com.kogo.kologbackend.domains.user.application.usecase.profile;

public record UserProfileResponse(
        Long userId,
        String nickname,
        String profileImage,
        String email,
        String createdAt
) {}
