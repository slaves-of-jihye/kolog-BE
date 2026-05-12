package com.kogo.kologbackend.application.log.internal;

import com.kogo.kologbackend.application.log.dto.response.LogGetHourList;

import java.util.List;

public interface LogGetHourListUseCase {
    List<LogGetHourList> getHourList();
}
