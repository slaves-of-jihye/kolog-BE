package com.kogo.kologbackend.domains.log.web;

import com.kogo.kologbackend.domains.log.application.usecase.chatCreate.ChatCreateCase;
import com.kogo.kologbackend.domains.log.application.usecase.chatCreate.ChatCreateRequest;
import com.kogo.kologbackend.domains.log.application.usecase.chatGetList.ChatGetListCase;
import com.kogo.kologbackend.domains.log.application.usecase.chatGetList.ChatGetListResponse;
import com.kogo.kologbackend.domains.log.application.usecase.emotionCreate.EmotionCreateCase;
import com.kogo.kologbackend.domains.log.application.usecase.emotionCreate.EmotionCreateRequest;
import com.kogo.kologbackend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoController {

    private final EmotionCreateCase emotionCreateCase;
    private final ChatCreateCase chatCreateCase;
    private final ChatGetListCase chatGetListCase;


    @PostMapping("/emotion")
    public ResponseEntity<ApiResponse> emotionCreate(
            @AuthenticationPrincipal Long userId,
            @RequestBody EmotionCreateRequest request
    ) {
        emotionCreateCase.createEmotion(userId, request);

        return ResponseEntity.ok(new ApiResponse<>(200, "이모티콘 표시 성공", request.emotionId()));
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse> chatCreate(
            @AuthenticationPrincipal Long userId,
            @RequestBody ChatCreateRequest request
            ){
        chatCreateCase.createChat(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(200,"댓글 작성 성공",request.chatContent()));
    }

    @GetMapping("/{logId}/chat")
    public ResponseEntity<ApiResponse<List<ChatGetListResponse>>> getChatList(
            @PathVariable Long logId
    ) {
        List<ChatGetListResponse> data = chatGetListCase.getChatList(logId);
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "댓글 조회 성공",
                data
        ));
    }
}
