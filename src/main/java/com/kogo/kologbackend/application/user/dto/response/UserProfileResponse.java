package com.kogo.kologbackend.application.user.dto.response;

public record UserProfileResponse(
        Long userId,
        String nickname,
        String profileImage,
        String email,
        String createdAt
) {}
