package com.kogo.kologbackend.application.chat.usecase;

import com.kogo.kologbackend.application.chat.dto.response.ChatGetListResponse;
import com.kogo.kologbackend.application.chat.external.ChatRepository;
import com.kogo.kologbackend.application.chat.internal.ChatGetListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGetListCase implements ChatGetListUseCase {
    private final ChatRepository chatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatGetListResponse> getChatList(Long logId) {
        return chatRepository.findByLogId(logId).stream()
                .map(chat -> new ChatGetListResponse(
                        chat.getChatId(),
                        chat.getUser().getId(),
                        chat.getUser().getUserInfo().getNickname(),
                        chat.getUser().getUserInfo().getProfileImage(),
                        chat.getChatContent()
                ))
                .toList();
    }
}
