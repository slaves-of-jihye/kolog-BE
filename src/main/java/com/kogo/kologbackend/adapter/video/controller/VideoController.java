package com.kogo.kologbackend.adapter.video.controller;

import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.adapter.auth.provider.JwtProvider;
import com.kogo.kologbackend.application.chat.dto.request.ChatCreateRequest;
import com.kogo.kologbackend.application.chat.dto.response.ChatGetListResponse;
import com.kogo.kologbackend.application.chat.internal.ChatCreateUseCase;
import com.kogo.kologbackend.application.chat.internal.ChatGetListUseCase;
import com.kogo.kologbackend.application.emotion.dto.request.EmotionCreateRequest;
import com.kogo.kologbackend.application.emotion.internal.EmotionCreateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoController {

    private final EmotionCreateUseCase emotionCreateUseCase;
    private final ChatCreateUseCase chatCreateUseCase;
    private final ChatGetListUseCase chatGetListUseCase;
    private final JwtProvider jwtProvider;


    @PostMapping("/emotion")
    public ResponseEntity<ApiResponse> emotionCreate(
            @RequestHeader("Authorization") String token,
            @RequestBody EmotionCreateRequest request
    ) {
        String jwt = token.substring(7);
        Long userId = jwtProvider.getUserIdFromToken(jwt);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        emotionCreateUseCase.createEmotion(userId, request);

        return ResponseEntity.ok(new ApiResponse<>(200, "이모티콘 표시 성공", request.emotionId()));
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse> chatCreate(
            @RequestHeader("Authorization") String token,
            @RequestBody ChatCreateRequest request
            ){
        String jwt = token.substring(7);
        Long userId = jwtProvider.getUserIdFromToken(jwt);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

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
