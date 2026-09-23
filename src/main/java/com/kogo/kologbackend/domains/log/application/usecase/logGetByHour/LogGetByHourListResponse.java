package com.kogo.kologbackend.domains.log.application.usecase.logGetByHour;

import java.util.List;

public record LogGetByHourListResponse(
        List<Integer> hours,
        List<LogGetByHourResponse> logs
) {
}
