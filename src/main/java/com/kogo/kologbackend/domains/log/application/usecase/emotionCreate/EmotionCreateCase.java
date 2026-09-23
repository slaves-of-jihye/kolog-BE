package com.kogo.kologbackend.domains.log.application.usecase.emotionCreate;

import com.kogo.kologbackend.domains.log.domain.Emotion;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.log.infrastructure.EmotionRepository;
import com.kogo.kologbackend.domains.log.infrastructure.LogRepository;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmotionCreateCase {
    private final UserJpaRepository userRepository;
    private final LogRepository logRepository;
    private final EmotionRepository emotionRepository;

    @Transactional
    public void createEmotion(Long userId, EmotionCreateRequest emotionCreateRequest) {
        if(emotionRepository.existsByLog_LogIdAndUserId(emotionCreateRequest.logId(), userId)) {
            throw new RuntimeException("이미 해당 로그에 감정표현을 남겼습니다.");
        }

        UserJpaEntity user = userRepository.findById(userId)
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
