package com.kogo.kologbackend.domains.log.application.log.dto.response;

import java.time.LocalDateTime;

public record LogCaptionUpdateResponse(
        Long logId,
        String caption,
        LocalDateTime updatedAt
) {
}
