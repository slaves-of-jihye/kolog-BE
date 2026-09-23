package com.kogo.kologbackend.domains.log.application.log.internal;

import com.kogo.kologbackend.domains.log.application.log.dto.request.LogCreateRequest;
import com.kogo.kologbackend.domains.log.application.log.dto.response.LogCreateResponse;

public interface LogCreateUseCase{
    LogCreateResponse logCreate(Long userId, LogCreateRequest logCreateRequest);


}
