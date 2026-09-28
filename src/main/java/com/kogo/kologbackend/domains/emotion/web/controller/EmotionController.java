package com.kogo.kologbackend.domains.emotion.web.controller;

import com.kogo.kologbackend.domains.emotion.application.usecase.crud.EmotionCreateUseCase;
import com.kogo.kologbackend.domains.emotion.application.usecase.crud.dto.response.EmotionResponse;
import com.kogo.kologbackend.domains.emotion.web.controller.dto.EmotionCreateWebRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs/{logId}/emotion")
public class EmotionController {
    private final EmotionCreateUseCase createUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmotionResponse createEmotion(@AuthenticationPrincipal UserDetail userDetail, @PathVariable Long logId,
                                          @RequestBody EmotionCreateWebRequest request) {
        return createUseCase.create(request.toApplication(logId, userDetail));
    }
}
