package com.kogo.kologbackend.domains.log.application.usecase.crud;

import com.kogo.kologbackend.domains.log.application.exception.LogNotFoundException;
import com.kogo.kologbackend.domains.log.application.exception.LogUpdateForbiddenException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.application.external.LogFileStorage;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.external.LogVideoValidator;
import com.kogo.kologbackend.domains.log.application.usecase.crud.dto.response.LogResponse;
import com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request.LogUpdateRequest;
import com.kogo.kologbackend.domains.log.domain.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LogUpdateUseCase {
    private final LogRepository logRepository;
    private final LogVideoValidator videoValidator;
    private final LogFileStorage files;

    @Transactional
    public LogResponse update(LogUpdateRequest request) {
        Log log = logRepository.findById(request.logId())
                .orElseThrow(LogNotFoundException::new);
        if (!log.uploader().id().equals(request.requester().userId())) {
            throw new LogUpdateForbiddenException();
        }

        String caption = request.caption() != null ? request.caption() : log.caption();
        String videoUrl = log.videoUrl();
        String previousVideoUrl = null;

        if (request.videoFile() != null) {
            try (BufferedInputStream video = new BufferedInputStream(request.videoFile())) {
                String mediaType = videoValidator.detectSupportedMediaType(video);
                videoUrl = files.storeVideo(video, mediaType);
                previousVideoUrl = log.videoUrl();
            } catch (IOException e) {
                throw new VideoUploadException("Failed to read or close the uploaded video.", e);
            }
        }

        Log updated = logRepository.save(new Log(log.id(), videoUrl, caption, log.date(), log.hour(), log.uploader()));

        if (previousVideoUrl != null) {
            files.deleteVideo(previousVideoUrl);
        }

        return LogResponse.from(updated);
    }
}
