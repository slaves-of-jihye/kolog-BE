package com.kogo.kologbackend.domains.emotion.domain;

import com.kogo.kologbackend.domains.user.domain.User;
import lombok.Builder;

@Builder
public record Emotion(
        Long id,
        String content,
        Long logId,
        User author
) {}
