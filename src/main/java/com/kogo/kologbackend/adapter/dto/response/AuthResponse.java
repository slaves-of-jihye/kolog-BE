package com.kogo.kologbackend.adapter.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        boolean isNewUser,
        UserDto user
) {
    public record UserDto(Long id, String email) {}
}