package com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

import java.io.InputStream;

@Builder
public record LogCreateRequest(
        InputStream videoFile,
        String caption,
        String date,
        Integer hour,
        UserDetail uploader
) {}
