package com.kogo.kologbackend.domains.log.application.chat.internal;

import com.kogo.kologbackend.domains.log.application.chat.dto.request.ChatCreateRequest;

public interface ChatCreateUseCase {
    void createChat(Long userId, ChatCreateRequest chatCreateRequest);
}
