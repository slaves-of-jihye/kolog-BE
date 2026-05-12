package com.kogo.kologbackend.application.log.dto.response;

import java.util.List;

public record LogGetByHourListResponse(
        List<Integer> hours,
        List<LogGetByHourResponse> logs
) {
}
