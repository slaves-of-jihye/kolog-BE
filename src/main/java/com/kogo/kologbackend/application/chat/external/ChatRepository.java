package com.kogo.kologbackend.application.chat.external;

import com.kogo.kologbackend.domain.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByLogId(Long logId);
}
