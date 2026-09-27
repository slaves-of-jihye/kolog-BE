package com.kogo.kologbackend.domains.comment.web.controller;

import com.kogo.kologbackend.domains.comment.application.usecase.crud.CommentCreateUseCase;
import com.kogo.kologbackend.domains.comment.application.usecase.crud.dto.response.CommentResponse;
import com.kogo.kologbackend.domains.comment.web.controller.dto.CommentCreateWebRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs/{logId}/comment")
public class CommentController {
    private final CommentCreateUseCase createUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@AuthenticationPrincipal UserDetail userDetail, @PathVariable Long logId,
                                          @RequestBody CommentCreateWebRequest request) {
        return createUseCase.create(request.toApplication(logId, userDetail));
    }
}
