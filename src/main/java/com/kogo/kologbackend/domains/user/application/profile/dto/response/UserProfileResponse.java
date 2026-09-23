package com.kogo.kologbackend.domains.user.application.profile.dto.response;

public record UserProfileResponse(
        Long userId,
        String nickname,
        String profileImage,
        String email,
        String createdAt
) {}
