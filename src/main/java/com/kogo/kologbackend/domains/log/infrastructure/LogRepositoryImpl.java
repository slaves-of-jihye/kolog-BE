package com.kogo.kologbackend.domains.log.infrastructure;

import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LogRepositoryImpl implements LogRepository {
    private final LogJpaRepository logs;
    private final UserJpaRepository users;

    @Override
    public Log save(Log log) {
        var user = users.getReferenceById(log.uploader().id());
        return LogMapper.toDomain(logs.save(LogMapper.toEntity(log, user)));
    }

}
