package com.kogo.kologbackend.domains.log.infrastructure;

import com.kogo.kologbackend.domains.log.domain.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    boolean existsByLog_LogIdAndUserId(Long logId, Long userId);
}
