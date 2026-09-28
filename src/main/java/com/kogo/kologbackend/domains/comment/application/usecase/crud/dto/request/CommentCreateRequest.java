package com.kogo.kologbackend.domains.comment.application.usecase.crud.dto.request;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

@Builder
public record CommentCreateRequest(
        Long logId,
        UserDetail requester,
        String content
) {}
