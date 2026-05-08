package com.kogo.kologbackend.application.chat.internal;

import com.kogo.kologbackend.application.chat.dto.response.ChatGetListResponse;

import java.util.List;

public interface ChatGetListUseCase {
    List<ChatGetListResponse> getChatList(Long logId);
}
