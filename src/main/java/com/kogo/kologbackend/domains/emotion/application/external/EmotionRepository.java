package com.kogo.kologbackend.domains.emotion.application.external;

import com.kogo.kologbackend.domains.emotion.domain.Emotion;

import java.util.List;

public interface EmotionRepository {
    Emotion save(Emotion emotion);
    boolean existsByLogIdAndAuthorId(Long logId, Long authorId);
    List<Emotion> findByLogId(Long logId);
}
