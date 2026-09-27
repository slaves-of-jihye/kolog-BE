package com.kogo.kologbackend.domains.comment.application.usecase.crud.dto.response;

import com.kogo.kologbackend.domains.comment.domain.Comment;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.UserResponse;
import lombok.Builder;

@Builder
public record CommentResponse(
        Long id,
        UserResponse author,
        String content
) {
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.id())
                .author(UserResponse.from(comment.author()))
                .content(comment.content())
                .build();
    }
}
