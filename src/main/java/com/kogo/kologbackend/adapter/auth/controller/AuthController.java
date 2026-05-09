package com.kogo.kologbackend.adapter.auth.controller;

import com.kogo.kologbackend.adapter.auth.Service.AuthService;
import com.kogo.kologbackend.adapter.auth.dto.request.SignupRequest;
import com.kogo.kologbackend.adapter.auth.dto.response.ApiResponse;
import com.kogo.kologbackend.adapter.auth.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
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
}
