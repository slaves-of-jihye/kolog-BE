package com.kogo.kologbackend.domains.user.application.usecase.crud.dto.request;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

import java.io.InputStream;

@Builder
public record UserProfileUpdateRequest(
        UserDetail requester,
        String nickname,
        InputStream profileImage
) {}
