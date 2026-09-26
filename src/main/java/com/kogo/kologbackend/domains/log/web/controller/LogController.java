package com.kogo.kologbackend.domains.log.web.controller;

import com.kogo.kologbackend.domains.log.application.usecase.crud.dto.response.LogResponse;
import com.kogo.kologbackend.domains.log.application.usecase.crud.LogCreateCase;
import com.kogo.kologbackend.domains.log.application.usecase.crud.LogGetUseCase;
import com.kogo.kologbackend.domains.log.application.usecase.crud.LogListUseCase;
import com.kogo.kologbackend.domains.log.application.usecase.crud.dto.request.LogListRequest;
import com.kogo.kologbackend.domains.log.application.usecase.crud.LogUpdateUseCase;
import com.kogo.kologbackend.domains.log.web.controller.dto.LogCreateWebRequest;
import com.kogo.kologbackend.domains.log.web.controller.dto.LogUpdateWebRequest;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
public class LogController {
    private final LogCreateCase createCase;
    private final LogListUseCase listUseCase;
    private final LogGetUseCase getUseCase;
    private final LogUpdateUseCase updateUseCase;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public LogResponse createLog(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute LogCreateWebRequest request) {
        return createCase.logCreate(request.toApplication(userDetail));
    }

    @GetMapping
    public List<LogResponse> listLogs(@RequestParam(required = false) String date, @RequestParam(required = false) Integer hour) {
        return listUseCase.list(LogListRequest.builder().date(date).hour(hour).build());
    }

    @GetMapping("/{logId}")
    public LogResponse getLog(@PathVariable Long logId) {
        return getUseCase.get(logId);
    }

    @PatchMapping(value = "/{logId}", consumes = "multipart/form-data")
    public LogResponse updateLog(@AuthenticationPrincipal UserDetail userDetail, @PathVariable Long logId,
                                 @ModelAttribute LogUpdateWebRequest request) {
        return updateUseCase.update(request.toApplication(logId, userDetail));
    }

}
