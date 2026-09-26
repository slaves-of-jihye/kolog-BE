package com.kogo.kologbackend.domains.log.infrastructure.jpa;

import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserMapper;

public final class LogMapper {
    private LogMapper() {}

    public static Log toDomain(LogJpaEntity entity) {
        return new Log(entity.getId(), entity.getVideoUrl(), entity.getCaption(),
                entity.getDate(), entity.getHour(), UserMapper.toDomain(entity.getUser()));
    }

    public static LogJpaEntity toEntity(Log log, UserJpaEntity user) {
        return LogJpaEntity.builder().id(log.id()).videoUrl(log.videoUrl())
                .caption(log.caption()).date(log.date()).hour(log.hour()).user(user).build();
    }
}
