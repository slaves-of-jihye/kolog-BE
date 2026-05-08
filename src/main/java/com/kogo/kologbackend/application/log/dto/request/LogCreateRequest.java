package com.kogo.kologbackend.application.log.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record LogCreateRequest(
        MultipartFile videoFile,
        String caption,
        String date,
        Integer hour
){}
