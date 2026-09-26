package com.kogo.kologbackend.domains.log.application.usecase.crud;

import com.kogo.kologbackend.domains.log.application.exception.LogDeleteForbiddenException;
import com.kogo.kologbackend.domains.log.application.exception.LogNotFoundException;
import com.kogo.kologbackend.domains.log.application.external.LogFileStorage;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request.LogDeleteRequest;
import com.kogo.kologbackend.domains.log.domain.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogDeleteUseCase {
    private final LogRepository logRepository;
    private final LogFileStorage files;

    @Transactional
    public void delete(LogDeleteRequest request) {
        Log log = logRepository.findById(request.logId())
                .orElseThrow(LogNotFoundException::new);
        if (!log.uploader().id().equals(request.requester().userId())) {
            throw new LogDeleteForbiddenException();
        }

        logRepository.deleteById(log.id());
        files.deleteVideo(log.videoUrl());
    }
}
