package com.kogo.kologbackend.domains.log.application.usecase.logList;

import com.kogo.kologbackend.domains.log.application.exception.InvalidLogDateException;
import com.kogo.kologbackend.domains.log.application.exception.InvalidLogHourException;
import com.kogo.kologbackend.domains.log.application.exception.InvalidLogQueryException;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.application.usecase.dto.LogResponse;
import com.kogo.kologbackend.domains.log.application.usecase.dto.LogUploaderResponse;
import com.kogo.kologbackend.domains.log.application.usecase.logList.dto.LogListRequest;
import com.kogo.kologbackend.domains.log.domain.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogListUseCase {
    private final LogRepository logRepository;

    public List<LogResponse> list(LogListRequest request) {
        LocalDateTime now = LocalDateTime.now();

        boolean dateProvided = request.date() != null && !request.date().isBlank();
        Integer requestedHour = request.hour();
        if (!dateProvided && requestedHour != null) {
            throw new InvalidLogQueryException("hour 파라미터는 date와 함께 제공되어야 합니다.");
        }
        if (requestedHour != null && (requestedHour < 0 || requestedHour > 23)) {
            throw new InvalidLogHourException();
        }

        LocalDate date;
        if (dateProvided) {
            try {
                date = LocalDate.parse(request.date());
            } catch (DateTimeParseException e) {
                throw new InvalidLogDateException();
            }
        } else {
            date = now.toLocalDate();
        }

        List<Log> logs;
        if (dateProvided && requestedHour == null) {
            logs = logRepository.findByDate(date);
        } else {
            Integer hour = requestedHour != null ? requestedHour : now.getHour();
            logs = logRepository.findByDateAndHour(date, hour);
        }

        return logs.stream()
                .sorted(Comparator.comparing(Log::date).thenComparing(Log::hour))
                .map(log -> LogResponse.builder()
                        .id(log.id())
                        .uploader(LogUploaderResponse.builder().id(log.uploader().id())
                                .nickname(log.uploader().nickname())
                                .profileImageUrl(log.uploader().profileImageUrl()).build())
                        .videoUrl(log.videoUrl())
                        .caption(log.caption())
                        .date(log.date())
                        .hour(log.hour())
                        .build())
                .toList();
    }
}
