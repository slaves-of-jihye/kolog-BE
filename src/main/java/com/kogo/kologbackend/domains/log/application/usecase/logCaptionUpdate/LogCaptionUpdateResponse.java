package com.kogo.kologbackend.domains.log.application.usecase.logCaptionUpdate;

import java.time.LocalDateTime;

public record LogCaptionUpdateResponse(
        Long logId,
        String caption,
        LocalDateTime updatedAt
) {
}
