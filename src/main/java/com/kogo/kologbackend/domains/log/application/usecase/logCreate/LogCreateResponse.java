package com.kogo.kologbackend.domains.log.application.usecase.logCreate;

public record LogCreateResponse(
    Long userId,
    Long logId,
    String videoUrl,
    String caption,
    Integer hour,
    String date
)
{}
