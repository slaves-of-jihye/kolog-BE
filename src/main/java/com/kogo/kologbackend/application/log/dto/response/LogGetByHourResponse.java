package com.kogo.kologbackend.application.log.dto.response;

import java.time.LocalDateTime;

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
