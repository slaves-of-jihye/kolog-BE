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
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getProfileImageUrl()
        );
    }
}
