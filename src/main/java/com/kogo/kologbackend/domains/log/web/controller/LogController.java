package com.kogo.kologbackend.domains.log.web.controller;

import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateCase;
import com.kogo.kologbackend.domains.log.application.usecase.logList.LogListUseCase;
import com.kogo.kologbackend.domains.log.application.usecase.logList.dto.LogListRequest;
import com.kogo.kologbackend.domains.log.application.usecase.logList.dto.LogListResponse;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.dto.LogCreateResponse;
import com.kogo.kologbackend.domains.log.web.controller.dto.LogCreateWebRequest;
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

    @PostMapping(value = "/video", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public LogCreateResponse createLog(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute LogCreateWebRequest request) {
        return createCase.logCreate(request.toApplication(userDetail));
    }

    @GetMapping
    public List<LogListResponse> listLogs(@RequestParam(required = false) String date, @RequestParam(required = false) Integer hour) {
        return listUseCase.list(LogListRequest.builder().date(date).hour(hour).build());
    }

}
