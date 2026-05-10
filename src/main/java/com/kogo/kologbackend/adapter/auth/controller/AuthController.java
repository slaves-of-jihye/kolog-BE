package com.kogo.kologbackend.adapter.auth.controller;

import com.kogo.kologbackend.adapter.auth.service.AuthService;
import com.kogo.kologbackend.adapter.auth.dto.request.LoginRequest;
import com.kogo.kologbackend.adapter.auth.dto.request.SignupRequest;
import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.adapter.auth.dto.response.AuthResponse;
import com.kogo.kologbackend.adapter.auth.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<AuthResponse.UserDto> signup(
            @RequestBody SignupRequest signupRequest
            ) {
        AuthResponse.UserDto data = authService.signup(signupRequest);
        return new ApiResponse<>(
                201,
                "회원가입 완료",
                data
        );
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
            ) {
        LoginResponse data = authService.login(loginRequest);
        return new ApiResponse<>(
                200,
                "로그인 성공",
                data
        );
    }
}
