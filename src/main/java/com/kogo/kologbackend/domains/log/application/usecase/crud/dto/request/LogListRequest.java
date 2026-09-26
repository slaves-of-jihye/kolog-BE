package com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request;

import lombok.Builder;

@Builder
public record LogListRequest(
        String date,
        Integer hour
) {}
