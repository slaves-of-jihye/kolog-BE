package com.kogo.kologbackend.domains.emotion.application.usecase.crud;

import com.kogo.kologbackend.domains.emotion.application.exception.DuplicateEmotionException;
import com.kogo.kologbackend.domains.emotion.application.exception.EmotionLogNotFoundException;
import com.kogo.kologbackend.domains.emotion.application.exception.EmotionUserNotFoundException;
import com.kogo.kologbackend.domains.emotion.application.exception.InvalidEmotionException;
import com.kogo.kologbackend.domains.emotion.application.external.EmotionRepository;
import com.kogo.kologbackend.domains.emotion.application.usecase.crud.dto.request.EmotionCreateRequest;
import com.kogo.kologbackend.domains.emotion.application.usecase.crud.dto.response.EmotionResponse;
import com.kogo.kologbackend.domains.emotion.domain.Emotion;
import com.kogo.kologbackend.domains.log.application.external.LogRepository;
import com.kogo.kologbackend.domains.log.domain.Log;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmotionCreateUseCase {
    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;
    private final LogRepository logRepository;

    @Transactional
    public EmotionResponse create(EmotionCreateRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new InvalidEmotionException();
        }
        if (emotionRepository.existsByLogIdAndAuthorId(request.logId(), request.requester().userId())) {
            throw new DuplicateEmotionException();
        }

        User author = userRepository.findById(request.requester().userId())
                .orElseThrow(EmotionUserNotFoundException::new);
        Log log = logRepository.findById(request.logId())
                .orElseThrow(EmotionLogNotFoundException::new);

        Emotion saved = emotionRepository.save(Emotion.builder()
                .content(request.content())
                .logId(log.id())
                .author(author)
                .build());
        return EmotionResponse.from(saved);
    }
}
