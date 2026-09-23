package com.kogo.kologbackend.domains.log.application.log.dto.response;

import java.util.List;

public record LogGetHourList(
        String date,
        List<Integer> hours
) {
}
