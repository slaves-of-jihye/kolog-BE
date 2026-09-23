package com.kogo.kologbackend.domains.log.application.usecase.logGetByHour;

import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.log.infrastructure.LogRepository;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogGetByHourCase {

    private final LogRepository logRepository;
    private final UserJpaRepository userRepository;

    @Transactional(readOnly = true)
    public LogGetByHourListResponse list(String date, Integer hour) {
        List<Integer> hours = logRepository.findHourByDate(date);

        List<Log> byDateAndHour = logRepository.findByDateAndHour(date, hour);

        List<LogGetByHourResponse> logResponses = byDateAndHour.stream()
                .map(log -> new LogGetByHourResponse(
                        log.getLogId(),
                        log.getVideoUrl(),
                        log.getCaption(),
                        log.getDate(),
                        log.getHour(),
                        log.getUser().getId(),
                        log.getUser().getNickname(),
                        log.getUser().getProfileImage()
                )).toList();

        return new LogGetByHourListResponse(hours, logResponses);
    }
}
