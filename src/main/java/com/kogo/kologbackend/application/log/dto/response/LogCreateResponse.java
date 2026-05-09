package com.kogo.kologbackend.application.log.dto.response;

import java.time.LocalDateTime;

public record LogCreateResponse(
    Long userId,
    Long logId,
    String videoUrl,
    String caption,
    Integer hour,
    String date
)
{}
