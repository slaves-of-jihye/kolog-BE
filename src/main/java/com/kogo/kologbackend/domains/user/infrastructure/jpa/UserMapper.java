package com.kogo.kologbackend.domains.user.infrastructure.jpa;

import com.kogo.kologbackend.domains.user.domain.User;

public final class UserMapper {
    private UserMapper() {}

    public static UserJpaEntity toEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.id())
                .email(user.email())
                .password(user.password())
                .nickname(user.nickname())
                .profileImageUrl(user.profileImageUrl())
                .build();
    }

    public static User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .profileImageUrl(entity.getProfileImageUrl())
                .build();
    }
}
