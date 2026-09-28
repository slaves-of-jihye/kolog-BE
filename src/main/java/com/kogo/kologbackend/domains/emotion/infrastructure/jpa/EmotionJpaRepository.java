package com.kogo.kologbackend.domains.emotion.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmotionJpaRepository extends JpaRepository<EmotionJpaEntity, Long> {
    boolean existsByLog_IdAndAuthor_Id(Long logId, Long authorId);
    List<EmotionJpaEntity> findByLog_IdOrderByIdAsc(Long logId);
    void deleteByLog_Id(Long logId);
}
