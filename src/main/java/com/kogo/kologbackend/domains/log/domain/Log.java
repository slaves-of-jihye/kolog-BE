package com.kogo.kologbackend.domains.log.domain;

import com.kogo.kologbackend.domains.user.domain.User;

import java.time.LocalDate;
public record Log(
        Long id,
        String videoUrl,
        String caption,
        LocalDate date,
        Integer hour,
        User uploader
) {
}
