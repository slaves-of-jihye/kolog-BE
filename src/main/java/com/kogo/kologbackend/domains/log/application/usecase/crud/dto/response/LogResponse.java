package com.kogo.kologbackend.domains.log.application.usecase.crud.dto.response;

import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.UserResponse;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LogResponse(
        Long id,
        UserResponse uploader,
        String videoUrl,
        String caption,
        LocalDate date,
        Integer hour
) {
    public static LogResponse from(Log log) {
        return LogResponse.builder()
                .id(log.id())
                .uploader(UserResponse.builder()
                        .id(log.uploader().id())
                        .nickname(log.uploader().nickname())
                        .profileImageUrl(log.uploader().profileImageUrl())
                        .build())
                .videoUrl(log.videoUrl())
                .caption(log.caption())
                .date(log.date())
                .hour(log.hour())
                .build();
    }
}
