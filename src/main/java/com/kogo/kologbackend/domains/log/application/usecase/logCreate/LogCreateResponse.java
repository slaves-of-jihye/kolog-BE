package com.kogo.kologbackend.domains.log.application.usecase.logCreate;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LogCreateResponse(
    Long id,
    String videoUrl,
    String caption,
    LocalDate date,
    Integer hour,
    Long uploaderId
) {}
