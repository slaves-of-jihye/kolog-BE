package com.kogo.kologbackend.domains.log.application.external;

import com.kogo.kologbackend.domains.log.domain.Log;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LogRepository {
    Log save(Log log);
    Optional<Log> findById(Long id);
    List<Log> findByDateAndHour(LocalDate date, Integer hour);
    List<Log> findByDate(LocalDate date);
    void deleteById(Long id);
}
