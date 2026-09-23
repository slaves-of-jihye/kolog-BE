package com.kogo.kologbackend.domains.log.web;

import com.kogo.kologbackend.domains.log.application.usecase.logCaptionUpdate.LogCaptionUpdateCase;
import com.kogo.kologbackend.domains.log.application.usecase.logCaptionUpdate.LogCaptionUpdateRequest;
import com.kogo.kologbackend.domains.log.application.usecase.logCaptionUpdate.LogCaptionUpdateResponse;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateCase;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateRequest;
import com.kogo.kologbackend.domains.log.application.usecase.logCreate.LogCreateResponse;
import com.kogo.kologbackend.domains.log.application.usecase.logDelete.LogDeleteCase;
import com.kogo.kologbackend.domains.log.application.usecase.logGetByHour.LogGetByHourCase;
import com.kogo.kologbackend.domains.log.application.usecase.logGetByHour.LogGetByHourListResponse;
import com.kogo.kologbackend.domains.log.application.usecase.logGetHourList.LogGetHourList;
import com.kogo.kologbackend.domains.log.application.usecase.logGetHourList.LogGetHourListCase;
import com.kogo.kologbackend.domains.log.application.usecase.logGetList.LogGetListCase;
import com.kogo.kologbackend.domains.log.application.usecase.logGetList.LogGetListResponse;
import com.kogo.kologbackend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
public class LogController {

    private final LogGetListCase logGetListCase;
    private final LogGetByHourCase logGetByHourCase;
    private final LogCreateCase logCreateCase;
    private final LogCaptionUpdateCase logCaptionUpdateCase;
    private final LogGetHourListCase logGetHourListCase;
    private final LogDeleteCase logDeleteCase;

    @PostMapping(value = "/video", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<LogCreateResponse>> createLog(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute LogCreateRequest request
    ) {
        LogCreateResponse data = logCreateCase.logCreate(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "로그 생성 성공", data));
    }

    @GetMapping("/{date}")
    public ResponseEntity<ApiResponse<List<LogGetListResponse>>> LogListGet(
            @PathVariable String date
    ) {
        List<LogGetListResponse> list = logGetListCase.list(date);
        return ResponseEntity.ok(new ApiResponse<>(200, "조회 성공", list));
    }

    @GetMapping("/hour")
    public ResponseEntity<ApiResponse<LogGetByHourListResponse>> LogGetByHour(
            @RequestParam(name = "date") String date,
            @RequestParam(name = "hour") Integer hour
    ) {
        LogGetByHourListResponse list = logGetByHourCase.list(date, hour);
        return ResponseEntity.ok(new ApiResponse<>(200, String.format("%d시 전체 로그 조회 성공", hour), list));
    }

    @PatchMapping("/{logId}/caption")
    public ResponseEntity<ApiResponse<LogCaptionUpdateResponse>> updateCaption(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long logId,
            @RequestBody LogCaptionUpdateRequest logCaptionUpdateRequest
    ) {
        LogCaptionUpdateResponse data = logCaptionUpdateCase.updateCaption(logId, userId, logCaptionUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(200, "캡션 수정 성공", data));
    }

    @GetMapping("/hours")
    public ResponseEntity<ApiResponse<List<LogGetHourList>>> getHourList() {
        List<LogGetHourList> hourList = logGetHourListCase.getHourList();
        return ResponseEntity.ok(new ApiResponse<>(200, "시간 목록 조회 성공", hourList));
    }

    @DeleteMapping("/delete/{logId}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long logId, @AuthenticationPrincipal Long userId) {
        logDeleteCase.deleteLog(logId, userId);
        return ResponseEntity.noContent().build();
    }
}
