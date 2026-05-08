package com.kogo.kologbackend.application.log.internal;

import com.kogo.kologbackend.application.log.dto.request.LogCreateRequest;
import com.kogo.kologbackend.application.log.dto.response.LogCreateResponse;

public interface LogCreateUseCase{
    LogCreateResponse logCreate(Long userId, LogCreateRequest logCreateRequest);


}
