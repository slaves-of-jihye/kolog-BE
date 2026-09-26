package com.kogo.kologbackend.domains.log.application.usecase.logCreate;

import lombok.Builder;

import java.io.InputStream;

@Builder
public record LogCreateRequest(
        InputStream videoFile,
        String caption,
        String date,
        Integer hour,
        Long uploaderId
) {}
