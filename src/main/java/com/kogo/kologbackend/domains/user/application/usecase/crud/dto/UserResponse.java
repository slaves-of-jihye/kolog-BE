package com.kogo.kologbackend.domains.user.application.usecase.crud.dto;

import com.kogo.kologbackend.domains.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.id())
                .nickname(user.nickname())
                .profileImageUrl(user.profileImageUrl())
                .build();
    }
}
