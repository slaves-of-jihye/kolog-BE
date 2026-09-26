package com.kogo.kologbackend.domains.log.application.external;

import com.kogo.kologbackend.domains.log.domain.Log;

public interface LogRepository {
    Log save(Log log);
}
