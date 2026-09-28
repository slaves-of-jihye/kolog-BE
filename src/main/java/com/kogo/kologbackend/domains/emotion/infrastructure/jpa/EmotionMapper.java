package com.kogo.kologbackend.domains.emotion.infrastructure.jpa;

import com.kogo.kologbackend.domains.emotion.domain.Emotion;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserMapper;

public final class EmotionMapper {
    private EmotionMapper() {}

    public static Emotion toDomain(EmotionJpaEntity entity) {
        return Emotion.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .logId(entity.getLog().getId())
                .author(UserMapper.toDomain(entity.getAuthor()))
                .build();
    }

    public static EmotionJpaEntity toEntity(Emotion emotion, LogJpaEntity log, UserJpaEntity author) {
        return EmotionJpaEntity.builder().id(emotion.id()).content(emotion.content())
                .log(log).author(author).build();
    }
}
