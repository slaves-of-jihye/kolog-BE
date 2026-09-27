package com.kogo.kologbackend.domains.comment.infrastructure.jpa;

import com.kogo.kologbackend.domains.comment.domain.Comment;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserMapper;

public final class CommentMapper {
    private CommentMapper() {}

    public static Comment toDomain(CommentJpaEntity entity) {
        return Comment.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .logId(entity.getLog().getId())
                .author(UserMapper.toDomain(entity.getAuthor()))
                .build();
    }

    public static CommentJpaEntity toEntity(Comment comment, LogJpaEntity log, UserJpaEntity author) {
        return CommentJpaEntity.builder().id(comment.id()).content(comment.content())
                .log(log).author(author).build();
    }
}
