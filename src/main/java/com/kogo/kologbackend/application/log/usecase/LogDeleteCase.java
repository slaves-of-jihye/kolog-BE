package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.domain.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LogDeleteCase {


    private final LogRepository logRepository;

    public void deleteLog(Long logId, Long userId) {
        Log log = logRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("로그가 존재 x"));

        if(!log.getUser().getId().equals(userId)) {
            throw new RuntimeException("업다 로그가");
        }

        logRepository.deleteById(logId);
    }

}
