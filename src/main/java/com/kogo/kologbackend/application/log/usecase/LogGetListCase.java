package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogGetListUseCase;
import com.kogo.kologbackend.domain.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LogGetListCase implements LogGetListUseCase {

    private final LogRepository logRepository;

    @Override
    public List<LogGetListResponse> list(String date) {
        List<Log> byDate = logRepository.findByDate(date);

        Map<String, List<Log>> groupedByDate = byDate.stream()
                .collect(Collectors.groupingBy(Log::getDate));
        return groupedByDate.values().stream()
                .map(logsInDate -> {
                    Collections.shuffle(logsInDate); // 그룹 내 데이터를 무작위로 섞음
                    return logsInDate.get(0);        // 첫 번째 데이터 추출
                })
                .map(log -> new LogGetListResponse(
                        log.getDate(),
                        log.getHour(),
                        log.getUser().getId()
                ))
                .toList(); // 결과: 각 날짜당 1개의 랜덤 로그가 담긴 배열
    }


}
