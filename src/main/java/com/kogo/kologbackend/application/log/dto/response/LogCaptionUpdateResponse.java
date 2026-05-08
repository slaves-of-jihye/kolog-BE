package com.kogo.kologbackend.application.log.dto.response;

import java.time.LocalDateTime;

public record LogCaptionUpdateResponse(
        Long logId,
        String caption,
        LocalDateTime updatedAt
) {
}
