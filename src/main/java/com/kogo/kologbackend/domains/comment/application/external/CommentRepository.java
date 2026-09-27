package com.kogo.kologbackend.domains.comment.application.external;

import com.kogo.kologbackend.domains.comment.domain.Comment;

import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findByLogId(Long logId);
}
