package com.kogo.kologbackend.domains.log.application.usecase.logGet;

import com.kogo.kologbackend.domains.log.application.exception.LogNotFoundException;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.usecase.dto.LogResponse;
import com.kogo.kologbackend.domains.log.application.usecase.dto.LogUploaderResponse;
import com.kogo.kologbackend.domains.log.domain.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogGetUseCase {
    private final LogRepository logRepository;

    public LogResponse get(Long logId) {
        Log log = logRepository.findById(logId)
                .orElseThrow(LogNotFoundException::new);

        return LogResponse.builder()
                .id(log.id())
                .uploader(LogUploaderResponse.builder().id(log.uploader().id())
                        .nickname(log.uploader().nickname())
                        .profileImageUrl(log.uploader().profileImageUrl()).build())
                .videoUrl(log.videoUrl())
                .caption(log.caption())
                .date(log.date())
                .hour(log.hour())
                .build();
    }
}
