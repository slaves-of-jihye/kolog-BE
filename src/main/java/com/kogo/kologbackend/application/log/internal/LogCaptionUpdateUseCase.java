package com.kogo.kologbackend.application.log.internal;

import com.kogo.kologbackend.application.log.dto.request.LogCaptionUpdateRequest;
import com.kogo.kologbackend.application.log.dto.response.LogCaptionUpdateResponse;

public interface LogCaptionUpdateUseCase {
    LogCaptionUpdateResponse updateCaption(Long logId, LogCaptionUpdateRequest logCaptionUpdateRequest);
}
