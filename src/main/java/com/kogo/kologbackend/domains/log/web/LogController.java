package com.kogo.kologbackend.domains.log.web;

import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateCase;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateRequest;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateResponse;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
public class LogController {
    private final LogCreateCase createCase;

    @PostMapping(value = "/video", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public LogCreateResponse createLog(@AuthenticationPrincipal Long userId,
                                       @ModelAttribute LogCreateWebRequest request) {
        try {
            LogCreateRequest applicationRequest = request.toApplication(userId);
            try (InputStream stream = applicationRequest.videoFile()) {
                return createCase.logCreate(applicationRequest);
            }
        } catch (IOException e) {
            throw new VideoUploadException("Failed to read or store the uploaded video.", e);
        }
    }

}
