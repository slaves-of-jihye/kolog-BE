package com.kogo.kologbackend.application.chat.internal;

import com.kogo.kologbackend.application.chat.dto.request.ChatCreateRequest;
import com.kogo.kologbackend.domain.chat.Chat;

public interface ChatCreateUseCase {
    void createChat(ChatCreateRequest chatCreateRequest);
}
