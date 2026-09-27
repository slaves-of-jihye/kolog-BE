package com.kogo.kologbackend.domains.log.infrastructure.jpa;

import com.kogo.kologbackend.domains.comment.domain.Comment;
import com.kogo.kologbackend.domains.emotion.domain.Emotion;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserMapper;

import java.util.List;

public final class LogMapper {
    private LogMapper() {}

    public static Log toDomain(LogJpaEntity entity, List<Comment> comments, List<Emotion> emotions) {
        return Log.builder()
                .id(entity.getId())
                .videoUrl(entity.getVideoUrl())
                .caption(entity.getCaption())
                .date(entity.getDate())
                .hour(entity.getHour())
                .uploader(UserMapper.toDomain(entity.getUploader()))
                .comments(comments)
                .emotions(emotions)
                .build();
    }

    public static LogJpaEntity toEntity(Log log, UserJpaEntity user) {
        return LogJpaEntity.builder().id(log.id()).videoUrl(log.videoUrl())
                .caption(log.caption()).date(log.date()).hour(log.hour()).uploader(user).build();
    }
}
