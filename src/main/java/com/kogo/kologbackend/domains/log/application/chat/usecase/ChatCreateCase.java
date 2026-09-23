package com.kogo.kologbackend.domains.log.application.chat.usecase;

import com.kogo.kologbackend.domains.log.application.chat.dto.request.ChatCreateRequest;
import com.kogo.kologbackend.domains.log.application.chat.external.ChatRepository;
import com.kogo.kologbackend.domains.log.application.chat.internal.ChatCreateUseCase;
import com.kogo.kologbackend.domains.log.application.log.external.LogRepository;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaRepository;
import com.kogo.kologbackend.domains.log.domain.Chat;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatCreateCase implements ChatCreateUseCase {

    private final ChatRepository chatRepository;
    private final UserJpaRepository userRepository;
    private final LogRepository logRepository;

    @Override
    public void createChat(Long userId, ChatCreateRequest chatCreateRequest) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Log log = logRepository.findById(chatCreateRequest.logId())
                .orElseThrow(() -> new RuntimeException("로그를 찾을 수 없습니다."));;


        Chat chat = Chat.builder().user(user).log(log).chatContent(chatCreateRequest.chatContent()).build();

        chatRepository.save(chat);


    }
}
