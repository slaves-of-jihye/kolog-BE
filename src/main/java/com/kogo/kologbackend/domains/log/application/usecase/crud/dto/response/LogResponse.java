package com.kogo.kologbackend.domains.log.application.usecase.crud.dto.response;

import com.kogo.kologbackend.domains.comment.application.usecase.crud.dto.response.CommentResponse;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.UserResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record LogResponse(
        Long id,
        UserResponse uploader,
        String videoUrl,
        String caption,
        LocalDate date,
        Integer hour,
        List<CommentResponse> comments
) {
    public static LogResponse from(Log log) {
        return LogResponse.builder()
                .id(log.id())
                .uploader(UserResponse.from(log.uploader()))
                .videoUrl(log.videoUrl())
                .caption(log.caption())
                .date(log.date())
                .hour(log.hour())
                .comments(log.comments().stream().map(CommentResponse::from).toList())
                .build();
    }
}
