package com.kogo.kologbackend.adapter.log;

import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.application.log.dto.response.LogGetByHourResponse;
import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
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

    @GetMapping("/{date}")
    public ResponseEntity<ApiResponse<List<LogGetListResponse>>> LogListGet(
            @PathVariable String date
    ) {
        List<LogGetListResponse> list = logGetListUseCase.list(date);
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "조회 성공",
                list
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LogGetByHourResponse>>> LogGetByHour(
            @RequestParam String date,
            @RequestParam Integer hour
    ){
        List<LogGetByHourResponse> list = logGetByHourUseCase.list(date,hour);
        return ResponseEntity.ok(new ApiResponse<>(200,String.format("%s시 전체 로그 조회 성공",hour),list));
    }
}
