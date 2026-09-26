package com.kogo.kologbackend.domains.log.web;

import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateCase;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateResponse;
import com.kogo.kologbackend.domains.log.web.dto.LogCreateWebRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
public class LogController {
    private final LogCreateCase createCase;

    @PostMapping(value = "/video", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public LogCreateResponse createLog(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute LogCreateWebRequest request) {
        return createCase.logCreate(request.toApplication(userDetail));
    }

}
