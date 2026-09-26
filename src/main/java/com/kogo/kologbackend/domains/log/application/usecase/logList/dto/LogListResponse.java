package com.kogo.kologbackend.domains.log.application.usecase.logList.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LogListResponse(
        Long id,
        Long uploaderId,
        String nickname,
        String profileImageUrl,
        String videoUrl,
        String caption,
        LocalDate date,
        Integer hour
) {}
