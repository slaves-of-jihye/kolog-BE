package com.kogo.kologbackend.domains.log.web.controller.dto;

import com.kogo.kologbackend.domains.log.application.exception.InvalidLogUpdateException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request.LogUpdateRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Builder
public record LogUpdateWebRequest(
        String caption,
        MultipartFile videoFile
) {
    public LogUpdateRequest toApplication(Long logId, UserDetail requester) {
        boolean hasCaption = caption != null && !caption.isBlank();
        boolean hasVideo = videoFile != null && !videoFile.isEmpty();
        if (!hasCaption && !hasVideo) {
            throw new InvalidLogUpdateException("caption 또는 videoFile 중 하나는 있어야 합니다.");
        }

        try {
            return LogUpdateRequest.builder()
                    .logId(logId)
                    .requester(requester)
                    .caption(hasCaption ? caption : null)
                    .videoFile(hasVideo ? videoFile.getInputStream() : null)
                    .build();
        } catch (IOException e) {
            throw new VideoUploadException("Failed to open the uploaded video.", e);
        }
    }
}
