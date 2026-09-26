package com.kogo.kologbackend.domains.log.application.usecase.logCreate;

import com.kogo.kologbackend.domains.log.application.exception.InvalidLogDateException;
import com.kogo.kologbackend.domains.log.application.exception.LogUserNotFoundException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.application.external.LogFileStorage;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.external.LogVideoValidator;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.dto.LogCreateRequest;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.dto.LogCreateResponse;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class LogCreateCase {
    private final LogRepository logRepository;
    private final UserRepository users;
    private final LogVideoValidator videoValidator;
    private final LogFileStorage files;

    @Transactional
    public LogCreateResponse logCreate(LogCreateRequest request) {
        try (BufferedInputStream video = new BufferedInputStream(request.videoFile())) {
            var user = users.findById(request.uploader().userId())
                    .orElseThrow(LogUserNotFoundException::new);
            LocalDate date;
            try {
                date = LocalDate.parse(request.date());
            } catch (DateTimeParseException | NullPointerException e) {
                throw new InvalidLogDateException();
            }

            String mediaType = videoValidator.detectSupportedMediaType(video);
            String videoUrl = files.storeVideo(video, mediaType);
            Log saved = logRepository.save(new Log(null, videoUrl, request.caption(), date, request.hour(), user));
            return LogCreateResponse.builder().id(saved.id()).videoUrl(saved.videoUrl())
                    .caption(saved.caption()).date(saved.date()).hour(saved.hour())
                    .uploaderId(user.id()).build();
        } catch (IOException e) {
            throw new VideoUploadException("Failed to read or close the uploaded video.", e);
        }
    }
}
