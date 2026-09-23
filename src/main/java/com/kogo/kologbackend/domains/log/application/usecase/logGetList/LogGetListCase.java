package com.kogo.kologbackend.domains.log.application.usecase.logGetList;

import com.kogo.kologbackend.domains.log.infrastructure.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class LogGetListCase {

    private final LogRepository logRepository;

    @Transactional(readOnly = true)
    public List<LogGetListResponse> list(String date) {
        return logRepository.findByDate(date).stream()
                .map(log -> {
                    return new LogGetListResponse(
                            log.getDate(),
                            log.getHour(),
                            log.getUser().getId(),
                            log.getUser().getNickname(),
                            log.getUser().getProfileImage(),
                            log.getVideoUrl(),
                            log.getCaption()
                    );
                })
                .toList();
    }


}
