package com.kogo.kologbackend.application.log.internal;

import com.kogo.kologbackend.application.log.dto.response.LogGetByHourResponse;

import java.util.List;

public interface LogGetByHourUseCase {
    List<LogGetByHourResponse> list(String date, Integer hour);
}
