package com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

import java.io.InputStream;

@Builder
public record LogUpdateRequest(
        Long logId,
        UserDetail requester,
        String caption,
        InputStream videoFile
) {}
