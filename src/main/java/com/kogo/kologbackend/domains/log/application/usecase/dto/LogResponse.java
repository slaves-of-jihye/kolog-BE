package com.kogo.kologbackend.domains.log.application.usecase.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LogResponse(
        Long id,
        LogUploaderResponse uploader,
        String videoUrl,
        String caption,
        LocalDate date,
        Integer hour
) {}
