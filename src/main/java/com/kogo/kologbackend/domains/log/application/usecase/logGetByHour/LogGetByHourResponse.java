package com.kogo.kologbackend.domains.log.application.usecase.logGetByHour;

public record LogGetByHourResponse(
        Long logId,
        String videoUrl,
        String caption,
        String date,
        Integer hour,
        Long userId,
        String nickname,
        String profileImage
) {}
