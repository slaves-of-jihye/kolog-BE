package com.kogo.kologbackend.domains.log.application.log.internal;

import com.kogo.kologbackend.domains.log.application.log.dto.response.LogGetHourList;

import java.util.List;

public interface LogGetHourListUseCase {
    List<LogGetHourList> getHourList();
}
