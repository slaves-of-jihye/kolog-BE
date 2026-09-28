package com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

@Builder
public record LogDeleteRequest(
        Long logId,
        UserDetail requester
) {}
