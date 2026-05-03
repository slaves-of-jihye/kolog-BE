package com.kogo.kologbackend.adapter.controller;

import com.kogo.kologbackend.adapter.dto.request.GoogleLoginRequest;
import com.kogo.kologbackend.adapter.dto.response.ApiResponse;
import com.kogo.kologbackend.adapter.dto.response.AuthResponse;
import com.kogo.kologbackend.adapter.provider.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@RequestBody GoogleLoginRequest request) {
        AuthResponse authResponse = authService.googleLogin(request.code(), request.redirectUri());

        ApiResponse<AuthResponse> response = new ApiResponse<>(
                200,
                "구글 로그인 성공",
                authResponse
        );

        return ResponseEntity.ok(response);
    }
}