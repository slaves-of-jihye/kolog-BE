package com.kogo.kologbackend.domains.log.application.log.internal;

import com.kogo.kologbackend.domains.log.application.log.dto.response.LogGetListResponse;

import java.util.List;

public interface LogGetListUseCase {
    List<LogGetListResponse> list(String date);
}
