package com.kogo.kologbackend.domains.user.web;

import com.kogo.kologbackend.domains.user.application.usecase.profile.UserProfileGetCase;
import com.kogo.kologbackend.domains.user.application.usecase.profile.UserProfileUpdateCase;
import com.kogo.kologbackend.domains.user.application.usecase.profile.UserProfileResponse;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupRequest;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupResponse;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupUseCase;
import com.kogo.kologbackend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserSignupUseCase userSignupUseCase;
    private final UserProfileGetCase userProfileGetCase;
    private final UserProfileUpdateCase userProfileUpdateCase;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSignupResponse signup(@RequestBody UserSignupRequest request) {
        return userSignupUseCase.signup(request);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal Long userId
    ) {
        UserProfileResponse data = userProfileGetCase.getProfile(userId);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "프로필 조회 성공",
                data
        ));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        UserProfileResponse data = userProfileUpdateCase.updateProfile(userId, nickname, profileImage);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "프로필 설정 성공",
                data
        ));
    }
//
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody UserLoginRequest loginRequest) {
//        AuthTokens tokens = authService.login(loginRequest.email(), loginRequest.password());
//        UserLoginResponse data = new UserLoginResponse(tokens.getGrantType(), tokens.getAccessToken(), tokens.getRefreshToken());
//        return ResponseEntity.ok(new ApiResponse<>(
//                200,
//                "로그인 성공",
//                data
//        ));
//    }
}
