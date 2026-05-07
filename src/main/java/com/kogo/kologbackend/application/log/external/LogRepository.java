package com.kogo.kologbackend.application.log.external;

import com.kogo.kologbackend.domain.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log,Long> {
}
