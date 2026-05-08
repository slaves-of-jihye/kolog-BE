package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.request.LogCreateRequest;
import com.kogo.kologbackend.application.log.dto.response.LogCreateResponse;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogCreateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogCreateCase implements LogCreateUseCase {

    private final LogRepository  logRepository;

    @Override
    public LogCreateResponse logCreate(Long userId, LogCreateRequest logCreateRequest) {
        return null;
    }
}
