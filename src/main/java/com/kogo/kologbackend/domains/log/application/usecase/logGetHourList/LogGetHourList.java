package com.kogo.kologbackend.domains.log.application.usecase.logGetHourList;

import java.util.List;

public record LogGetHourList(
        String date,
        List<Integer> hours
) {
}
