package com.kogo.kologbackend.domains.log.infrastructure;

import com.kogo.kologbackend.domains.log.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByLog_LogId(Long logId);
}
