package com.kogo.kologbackend.domains.log.application.usecase.crud;

import com.kogo.kologbackend.domains.log.application.exception.LogNotFoundException;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.usecase.crud.dto.response.LogResponse;
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
        return LogResponse.from(log);
    }
}
