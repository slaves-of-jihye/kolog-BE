package com.kogo.kologbackend.domains.log.application.external;

import com.kogo.kologbackend.domains.log.domain.Log;

import java.time.LocalDate;
import java.util.List;

public interface LogRepository {
    Log save(Log log);
    List<Log> findByDateAndHour(LocalDate date, Integer hour);
    List<Log> findByDate(LocalDate date);
}
