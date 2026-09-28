package com.kogo.kologbackend.domains.comment.web.controller.dto;

import com.kogo.kologbackend.domains.comment.application.usecase.crud.dto.request.CommentCreateRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.Builder;

@Builder
public record CommentCreateWebRequest(String content) {
    public CommentCreateRequest toApplication(Long logId, UserDetail requester) {
        return CommentCreateRequest.builder()
                .logId(logId)
                .requester(requester)
                .content(content)
                .build();
    }
}
