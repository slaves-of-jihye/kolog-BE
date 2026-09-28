package com.kogo.kologbackend.domains.emotion.infrastructure.adapter;

import com.kogo.kologbackend.domains.emotion.application.external.EmotionRepository;
import com.kogo.kologbackend.domains.emotion.domain.Emotion;
import com.kogo.kologbackend.domains.emotion.infrastructure.jpa.EmotionJpaRepository;
import com.kogo.kologbackend.domains.emotion.infrastructure.jpa.EmotionMapper;
import com.kogo.kologbackend.domains.log.infrastructure.jpa.LogJpaRepository;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmotionRepositoryAdapter implements EmotionRepository {
    private final EmotionJpaRepository emotions;
    private final LogJpaRepository logs;
    private final UserJpaRepository users;

    @Override
    public Emotion save(Emotion emotion) {
        var log = logs.getReferenceById(emotion.logId());
        var author = users.getReferenceById(emotion.author().id());
        return EmotionMapper.toDomain(emotions.save(EmotionMapper.toEntity(emotion, log, author)));
    }

    @Override
    public boolean existsByLogIdAndAuthorId(Long logId, Long authorId) {
        return emotions.existsByLog_IdAndAuthor_Id(logId, authorId);
    }

    @Override
    public List<Emotion> findByLogId(Long logId) {
        return emotions.findByLog_IdOrderByIdAsc(logId).stream().map(EmotionMapper::toDomain).toList();
    }
}
