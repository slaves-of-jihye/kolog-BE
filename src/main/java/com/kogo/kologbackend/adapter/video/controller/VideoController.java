package com.kogo.kologbackend.adapter.video.controller;

import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.application.chat.dto.request.ChatCreateRequest;
import com.kogo.kologbackend.application.chat.dto.response.ChatGetListResponse;
import com.kogo.kologbackend.application.chat.internal.ChatCreateUseCase;
import com.kogo.kologbackend.application.chat.internal.ChatGetListUseCase;
import com.kogo.kologbackend.application.emotion.dto.request.EmotionCreateRequest;
import com.kogo.kologbackend.application.emotion.internal.EmotionCreateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoController {

    private final EmotionCreateUseCase emotionCreateUseCase;
    private final ChatCreateUseCase chatCreateUseCase;
    private final ChatGetListUseCase chatGetListUseCase;


    @PostMapping("/emotion")
    public ResponseEntity<ApiResponse> emotionCreate(
            @AuthenticationPrincipal Long userId,
            @RequestBody EmotionCreateRequest request
    ) {
        emotionCreateUseCase.createEmotion(userId, request);

        return ResponseEntity.ok(new ApiResponse<>(200, "이모티콘 표시 성공", request.emotionId()));
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse> chatCreate(
            @AuthenticationPrincipal Long userId,
            @RequestBody ChatCreateRequest request
            ){
        chatCreateUseCase.createChat(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(200,"댓글 작성 성공",request.chatContent()));
    }

    @GetMapping("/{logId}/chat")
    public ResponseEntity<ApiResponse<List<ChatGetListResponse>>> getChatList(
            @PathVariable Long logId
    ) {
        List<ChatGetListResponse> data = chatGetListUseCase.getChatList(logId);
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "댓글 조회 성공",
                data
        ));
    }
}
