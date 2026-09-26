package com.kogo.kologbackend.domains.log.web.dto;

import com.kogo.kologbackend.domains.log.application.exception.InvalidVideoException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Builder
public record LogCreateWebRequest(
        String caption,
        MultipartFile videoFile,
        String date,
        Integer term
) {
    public LogCreateRequest toApplication(UserDetail uploader) {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new InvalidVideoException("A video file is required.");
        }
        try {
            return LogCreateRequest.builder()
                    .videoFile(videoFile.getInputStream())
                    .caption(caption)
                    .date(date)
                    .hour(term)
                    .uploader(uploader).build();
        } catch (IOException e) {
            throw new VideoUploadException("Failed to open the uploaded video.", e);
        }
    }
}
