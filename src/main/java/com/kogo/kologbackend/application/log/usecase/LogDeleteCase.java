package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.domain.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class LogDeleteCase {

    private final LogRepository logRepository;

    public void deleteLog(Long logId, Long userId) {
        Log log = logRepository.findById(logId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "로그를 찾을 수 없습니다."));

        if (!log.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("본인의 로그만 삭제할 수 있습니다.");
        }

        logRepository.deleteById(logId);
    }
}
