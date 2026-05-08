package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogGetListUseCase;
import com.kogo.kologbackend.domain.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LogGetListCase implements LogGetListUseCase {

    private final LogRepository logRepository;

    @Override
    public List<LogGetListResponse> list(String date) {
        List<Log> byDate = logRepository.findByDate(date);

        return byDate.stream()
                .map(log -> new LogGetListResponse(
                        log.getDate(),
                        log.getHour(),
                        log.getUser().getId()
                ))
                .toList();

    }
}
