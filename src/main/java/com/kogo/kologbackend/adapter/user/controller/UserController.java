package com.kogo.kologbackend.adapter.user.controller;

import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.adapter.auth.provider.JwtProvider;
import com.kogo.kologbackend.application.user.dto.response.UserProfileResponse;
import com.kogo.kologbackend.application.user.internal.UserProfileGetUseCase;
import com.kogo.kologbackend.application.user.internal.UserProfileUpdateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestControllerrm -f .git/index.lock
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserProfileUpdateUseCase userProfileUpdateUseCase;
    private final UserProfileGetUseCase userProfileGetUseCase;
    private final JwtProvider jwtProvider;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.substring(7);
        Long userId = jwtProvider.getUserIdFromToken(jwt);
        UserProfileResponse data = userProfileGetUseCase.getProfile(userId);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "프로필 조회 성공",
                data
        ));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value="nickname", required = false) String nickname,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
            ) {
        UserProfileResponse data = userProfileUpdateUseCase.updateProfile(userId, nickname, profileImage);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "프로필 설정 성공",
                data
        ));
    }
}
