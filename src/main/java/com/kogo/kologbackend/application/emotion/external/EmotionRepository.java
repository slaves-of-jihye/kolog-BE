package com.kogo.kologbackend.application.emotion.external;

import com.kogo.kologbackend.domain.emotion.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
}
