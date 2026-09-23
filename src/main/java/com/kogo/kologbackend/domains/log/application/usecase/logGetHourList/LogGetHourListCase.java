package com.kogo.kologbackend.domains.log.application.usecase.logGetHourList;

import com.kogo.kologbackend.domains.log.infrastructure.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogGetHourListCase {

    private final LogRepository logRepository;

    public List<LogGetHourList> getHourList() {
        List<LogHourRaw> hours = logRepository.findByHour();

        Map<String, List<Integer>> groupedData = hours.stream()
                .collect(Collectors.groupingBy(
                        LogHourRaw::date,
                        Collectors.mapping(LogHourRaw::hour, Collectors.toList())
                ));

        return groupedData.entrySet().stream()
                .map(entry -> new LogGetHourList(
                        entry.getKey(),   // date
                        entry.getValue()  // [1, 4, 7] 같은 리스트
                ))
                .collect(Collectors.toList());
    }
}
