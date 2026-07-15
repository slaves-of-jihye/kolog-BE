package com.kogo.kologbackend.application.log.dto.response;

import java.util.List;

public record LogGetHourList(
        String date,
        List<Integer> hours
) {
}
