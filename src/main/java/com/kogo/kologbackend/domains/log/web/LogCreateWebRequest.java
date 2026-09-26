package com.kogo.kologbackend.domains.log.web;

import com.kogo.kologbackend.domains.log.application.exception.InvalidVideoException;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateRequest;
import lombok.Builder;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record LogCreateWebRequest(
        String caption,
        MultipartFile videoFile,
        String date,
        Integer term
) {
    public LogCreateRequest toApplication(Long uploaderId) throws IOException {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new InvalidVideoException("A video file is required.");
        }
        return LogCreateRequest.builder().videoFile(videoFile.getInputStream()).caption(caption)
                .date(date).hour(term).uploaderId(uploaderId).build();
    }
}
