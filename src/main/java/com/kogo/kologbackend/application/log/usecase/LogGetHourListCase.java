package com.kogo.kologbackend.application.log.usecase;

import com.kogo.kologbackend.application.log.dto.response.LogGetHourList;
import com.kogo.kologbackend.application.log.dto.response.LogHourRaw;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.internal.LogGetHourListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogGetHourListCase implements LogGetHourListUseCase {

    private final LogRepository logRepository;

    @Override
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
