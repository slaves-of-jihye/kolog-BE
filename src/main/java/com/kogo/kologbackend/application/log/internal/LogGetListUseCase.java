package com.kogo.kologbackend.application.log.internal;

import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;

import java.util.List;

public interface LogGetListUseCase {
    List<LogGetListResponse> list();
}
