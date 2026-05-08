package com.kogo.kologbackend.application.chat.usecase;

import com.kogo.kologbackend.application.chat.dto.request.ChatCreateRequest;
import com.kogo.kologbackend.application.chat.external.ChatRepository;
import com.kogo.kologbackend.application.chat.internal.ChatCreateUseCase;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.chat.Chat;
import com.kogo.kologbackend.domain.log.Log;
import com.kogo.kologbackend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatCreateCase implements ChatCreateUseCase {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final LogRepository logRepository;

    @Override
    public void createChat(ChatCreateRequest chatCreateRequest) {
        User user = userRepository.findById(chatCreateRequest.userId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Log log = logRepository.findById(chatCreateRequest.logId())
                .orElseThrow(() -> new RuntimeException("로그를 찾을 수 없습니다."));;


        Chat chat = Chat.builder().user(user).log(log).chatContent(chatCreateRequest.chatContent()).build();

        chatRepository.save(chat);


    }
}
