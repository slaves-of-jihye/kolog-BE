package com.kogo.kologbackend.domains.log.application.usecase.chatGetList;

import com.kogo.kologbackend.domains.log.infrastructure.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGetListCase {
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<ChatGetListResponse> getChatList(Long logId) {
        return chatRepository.findByLog_LogId(logId).stream()
                .map(chat -> new ChatGetListResponse(
                        chat.getChatId(),
                        chat.getUser().getId(),
                        chat.getUser().getNickname(),
                        chat.getUser().getProfileImage(),
                        chat.getChatContent()
                ))
                .toList();
    }
}
