package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.response.LogGetByHourListResponse;
import com.kogo.kologbackend.application.log.dto.response.LogGetByHourResponse;
import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogGetByHourUseCase;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.log.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogGetByHourCase implements LogGetByHourUseCase {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Override
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
                        log.getUser().getUserInfo().getNickname(),
                        log.getUser().getUserInfo().getProfileImage()
                )).toList();

        return new LogGetByHourListResponse(hours, logResponses);
    }
}
