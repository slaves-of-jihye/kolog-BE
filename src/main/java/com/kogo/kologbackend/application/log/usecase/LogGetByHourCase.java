package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.response.LogGetByHourResponse;
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
    public List<LogGetByHourResponse> list(String date,Integer hour) {
        List<Log> byDateAndHour = logRepository.findByDateAndHour(date, hour);

        return byDateAndHour.stream()
                .map(log ->
                    new LogGetByHourResponse(
                    log.getLogId(),
                    log.getVideoUrl(),
                    log.getCaption(),
                    log.getDate(),
                    log.getHour(),
                    log.getUser().getId(),
                    log.getUser().getUserInfo().getNickname(),
                    log.getUser().getUserInfo().getProfileImage()
                    )
        ).toList();
    }
}
