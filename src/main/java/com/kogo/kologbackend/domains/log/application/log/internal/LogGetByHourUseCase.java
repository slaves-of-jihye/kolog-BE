package com.kogo.kologbackend.domains.log.application.log.internal;

import com.kogo.kologbackend.domains.log.application.log.dto.response.LogGetByHourListResponse;

public interface LogGetByHourUseCase {
    LogGetByHourListResponse list(String date, Integer hour);
}
