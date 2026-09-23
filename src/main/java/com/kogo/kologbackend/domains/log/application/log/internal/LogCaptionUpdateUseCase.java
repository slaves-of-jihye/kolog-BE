package com.kogo.kologbackend.domains.log.application.log.internal;

import com.kogo.kologbackend.domains.log.application.log.dto.request.LogCaptionUpdateRequest;
import com.kogo.kologbackend.domains.log.application.log.dto.response.LogCaptionUpdateResponse;

public interface LogCaptionUpdateUseCase {
    LogCaptionUpdateResponse updateCaption(Long logId, Long userId, LogCaptionUpdateRequest logCaptionUpdateRequest);
}
