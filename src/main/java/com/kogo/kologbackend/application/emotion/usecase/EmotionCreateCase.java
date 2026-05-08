package com.kogo.kologbackend.application.emotion.usecase;

import com.kogo.kologbackend.application.emotion.dto.request.EmotionCreateRequest;
import com.kogo.kologbackend.application.emotion.external.EmotionRepository;
import com.kogo.kologbackend.application.emotion.internal.EmotionCreateUseCase;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.emotion.Emotion;
import com.kogo.kologbackend.domain.log.Log;
import com.kogo.kologbackend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmotionCreateCase implements EmotionCreateUseCase {
    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final EmotionRepository emotionRepository;

    @Override
    @Transactional
    public void createEmotion(Long userId, EmotionCreateRequest emotionCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));

        Log log = logRepository.findById(emotionCreateRequest.logId())
                .orElseThrow(() -> new RuntimeException("해당하는 로그가 없습니다."));

        Emotion emotion = Emotion.builder()
                .emotionId(emotionCreateRequest.emotionId())
                .user(user)
                .log(log)
                .build();

        emotionRepository.save(emotion);
    }
}
