package com.kogo.kologbackend.domains.user.web;

import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupRequest;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupResponse;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserSignupUseCase userSignupUseCase;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSignupResponse signup(@RequestBody UserSignupRequest request) {
        return userSignupUseCase.signup(request);
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
