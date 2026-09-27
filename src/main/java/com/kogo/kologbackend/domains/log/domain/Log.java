package com.kogo.kologbackend.domains.log.domain;

import com.kogo.kologbackend.domains.comment.domain.Comment;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record Log(
        Long id,
        String videoUrl,
        String caption,
        LocalDate date,
        Integer hour,
        User uploader,
        List<Comment> comments
) {}
