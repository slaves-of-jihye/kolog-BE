package com.kogo.kologbackend.application.log.internal;

import com.kogo.kologbackend.application.log.dto.response.LogGetByHourListResponse;

public interface LogGetByHourUseCase {
    LogGetByHourListResponse list(String date, Integer hour);
}
