package com.kogo.kologbackend.adapter.log;

import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.application.log.dto.response.LogGetListResponse;
import com.kogo.kologbackend.application.log.internal.LogGetListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
public class LogController {
    private final LogGetListUseCase logGetListUseCase;

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
}
