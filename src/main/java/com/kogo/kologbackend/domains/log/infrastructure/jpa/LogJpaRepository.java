package com.kogo.kologbackend.domains.log.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LogJpaRepository extends JpaRepository<LogJpaEntity, Long> {
    List<LogJpaEntity> findByDateAndHour(LocalDate date, Integer hour);
    List<LogJpaEntity> findByDate(LocalDate date);
}
