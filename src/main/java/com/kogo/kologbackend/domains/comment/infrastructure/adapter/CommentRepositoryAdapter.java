package com.kogo.kologbackend.domains.comment.infrastructure.adapter;

import com.kogo.kologbackend.domains.comment.application.external.CommentRepository;
import com.kogo.kologbackend.domains.comment.domain.Comment;
import com.kogo.kologbackend.domains.comment.infrastructure.jpa.CommentJpaRepository;
import com.kogo.kologbackend.domains.comment.infrastructure.jpa.CommentMapper;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogJpaRepository;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {
    private final CommentJpaRepository comments;
    private final LogJpaRepository logs;
    private final UserJpaRepository users;

    @Override
    public Comment save(Comment comment) {
        var log = logs.getReferenceById(comment.logId());
        var author = users.getReferenceById(comment.author().id());
        return CommentMapper.toDomain(comments.save(CommentMapper.toEntity(comment, log, author)));
    }

    @Override
    public List<Comment> findByLogId(Long logId) {
        return comments.findByLog_IdOrderByIdAsc(logId).stream().map(CommentMapper::toDomain).toList();
    }
}
