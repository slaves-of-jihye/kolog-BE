package com.kogo.kologbackend.domains.log.application.log.usecase;

import com.kogo.kologbackend.domains.log.application.log.dto.request.LogCaptionUpdateRequest;
import com.kogo.kologbackend.domains.log.application.log.dto.response.LogCaptionUpdateResponse;
import com.kogo.kologbackend.domains.log.application.log.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.log.internal.LogCaptionUpdateUseCase;
import com.kogo.kologbackend.domains.log.domain.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogCaptionUpdateCase implements LogCaptionUpdateUseCase {
    private final LogRepository logRepository;

    @Override
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
