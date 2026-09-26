package com.kogo.kologbackend.domains.log.infrastructure.adapter;

import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogJpaRepository;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogMapper;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LogRepositoryAdapter implements LogRepository {
    private final LogJpaRepository logs;
    private final UserJpaRepository users;

    @Override
    public Log save(Log log) {
        var user = users.getReferenceById(log.uploader().id());
        return LogMapper.toDomain(logs.save(LogMapper.toEntity(log, user)));
    }

    @Override
    public Optional<Log> findById(Long id) {
        return logs.findById(id).map(LogMapper::toDomain);
    }

    @Override
    public List<Log> findByDateAndHour(LocalDate date, Integer hour) {
        return logs.findByDateAndHour(date, hour).stream().map(LogMapper::toDomain).toList();
    }

    @Override
    public List<Log> findByDate(LocalDate date) {
        return logs.findByDate(date).stream().map(LogMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        logs.deleteById(id);
    }

}
