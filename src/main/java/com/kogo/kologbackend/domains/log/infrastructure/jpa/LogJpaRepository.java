package com.kogo.kologbackend.domains.log.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogJpaRepository extends JpaRepository<LogJpaEntity, Long> {
}
