package com.kogo.kologbackend.domains.log.application.usecase.logCreate;

import org.springframework.web.multipart.MultipartFile;

public record LogCreateRequest(
        MultipartFile videoFile,
        String caption,
        String date,
        Integer hour
){}
