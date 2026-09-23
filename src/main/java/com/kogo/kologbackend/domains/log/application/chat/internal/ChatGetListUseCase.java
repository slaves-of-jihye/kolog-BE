package com.kogo.kologbackend.domains.log.application.chat.internal;

import com.kogo.kologbackend.domains.log.application.chat.dto.response.ChatGetListResponse;

import java.util.List;

public interface ChatGetListUseCase {
    List<ChatGetListResponse> getChatList(Long logId);
}
