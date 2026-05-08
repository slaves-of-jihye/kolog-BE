package com.kogo.kologbackend.application.chat.external;

import com.kogo.kologbackend.domain.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
