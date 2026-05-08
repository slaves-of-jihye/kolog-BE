package com.kogo.kologbackend.application.log.dto.response;

import java.time.LocalDateTime;

public record LogGetListResponse(
        String date,
        Integer hour,
        Long userId,
        String nickname,
        String profileImage,
        String videoUrl,
        String caption
) {

}
