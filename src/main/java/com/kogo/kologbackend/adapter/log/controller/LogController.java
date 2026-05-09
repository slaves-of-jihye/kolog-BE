package com.kogo.kologbackend.adapter.log.controller;

import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.adapter.auth.provider.JwtProvider;
import com.kogo.kologbackend.application.log.dto.request.LogCaptionUpdateRequest;
import com.kogo.kologbackend.application.log.dto.request.LogCreateRequest;
import com.kogo.kologbackend.application.log.dto.response.LogCaptionUpdateResponse;
import com.kogo.kologbackend.application.log.dto.response.LogCreateResponse;
import com.kogo.kologbackend.application.log.dto.response.LogGetByHourResponse;
import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
import com.kogo.kologbackend.application.log.internal.LogCaptionUpdateUseCase;
import com.kogo.kologbackend.application.log.internal.LogCreateUseCase;
import com.kogo.kologbackend.application.log.internal.LogGetByHourUseCase;
import com.kogo.kologbackend.application.log.internal.LogGetListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
public class LogController {

    private final LogGetListUseCase logGetListUseCase;
    private final LogGetByHourUseCase logGetByHourUseCase;
    private final LogCreateUseCase logCreateUseCase;
    private final LogCaptionUpdateUseCase logCaptionUpdateUseCase;
    private final JwtProvider jwtProvider;

    @PostMapping(value="/video", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<LogCreateResponse>> createLog(
            @RequestHeader("Authorization") String token,
            @ModelAttribute LogCreateRequest request
    ) {
        String jwt = token.substring(7);
        Long userId = jwtProvider.getUserIdFromToken(jwt);
        LogCreateResponse data = logCreateUseCase.logCreate(userId, request);

        return ResponseEntity.ok(new ApiResponse<>(200, "로그 생성 성공", data));
    }

    @GetMapping("/{date}")
    public ResponseEntity<ApiResponse<List<LogGetListResponse>>> LogListGet(
            @PathVariable String date
    ) {
        List<LogGetListResponse> list = logGetListUseCase.list(date);
        return ResponseEntity.ok(new ApiResponse<>(200, "조회 성공", list));
    }

    @GetMapping("/hour")
    public ResponseEntity<ApiResponse<List<LogGetByHourResponse>>> LogGetByHour(
            @RequestParam String date,
            @RequestParam Integer hour
    ){
        List<LogGetByHourResponse> list = logGetByHourUseCase.list(date, hour);
        return ResponseEntity.ok(new ApiResponse<>(200, String.format("%d시 전체 로그 조회 성공", hour), list));
    }

    @PatchMapping("/{logId}/caption")
    public ResponseEntity<ApiResponse<LogCaptionUpdateResponse>> updateCaption(
            @RequestHeader("Authorization") String token,
            @PathVariable Long logId,
            @RequestBody LogCaptionUpdateRequest logCaptionUpdateRequest
    ) {
        String jwt = token.substring(7);
        Long userId = jwtProvider.getUserIdFromToken(jwt);
        LogCaptionUpdateResponse data = logCaptionUpdateUseCase.updateCaption(logId, userId, logCaptionUpdateRequest);

        return ResponseEntity.ok(new ApiResponse<>(200, "캡션 수정 성공", data));
    }
}