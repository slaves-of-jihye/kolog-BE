package com.kogo.kologbackend.domains.log.application.usecase.logCaptionUpdate;

import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.log.infrastructure.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogCaptionUpdateCase {
    private final LogRepository logRepository;

    @Transactional
    public LogCaptionUpdateResponse updateCaption(Long logId, Long userId, LogCaptionUpdateRequest logCaptionUpdateRequest) {
        Log log = logRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("로그가 존재하지 않습니다."));

        if (!log.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 로그만 수정할 수 있습니다.");
        }

        log.updateCaption(logCaptionUpdateRequest.caption());

        return new LogCaptionUpdateResponse(
                log.getLogId(),
                log.getCaption(),
                LocalDateTime.now()
        );
    }
}
