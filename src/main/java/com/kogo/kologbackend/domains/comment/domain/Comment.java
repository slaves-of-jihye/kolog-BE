package com.kogo.kologbackend.domains.comment.domain;

import com.kogo.kologbackend.domains.user.domain.User;
import lombok.Builder;

@Builder
public record Comment(
        Long id,
        String content,
        Long logId,
        User author
) {}
