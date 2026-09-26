package com.kogo.kologbackend.domains.log.infrastructure;

import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.UserMapper;

final class LogMapper {
    private LogMapper() {}

    static Log toDomain(LogJpaEntity entity) {
        return new Log(entity.getId(), entity.getVideoUrl(), entity.getCaption(),
                entity.getDate(), entity.getHour(), UserMapper.toDomain(entity.getUser()));
    }

    static LogJpaEntity toEntity(Log log, UserJpaEntity user) {
        return LogJpaEntity.builder().id(log.id()).videoUrl(log.videoUrl())
                .caption(log.caption()).date(log.date()).hour(log.hour()).user(user).build();
    }
}
