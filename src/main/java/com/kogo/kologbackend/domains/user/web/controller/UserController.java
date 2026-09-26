package com.kogo.kologbackend.domains.user.web.controller;

import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserLoginRequest;
import com.kogo.kologbackend.domains.user.application.usecase.auth.UserLoginUseCase;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserSignupRequest;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserAuthResponse;
import com.kogo.kologbackend.domains.user.application.usecase.auth.UserSignupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserSignupUseCase userSignupUseCase;
    private final UserLoginUseCase userLoginUseCase;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAuthResponse signup(@RequestBody UserSignupRequest request) {
        return userSignupUseCase.signup(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserAuthResponse login(@RequestBody UserLoginRequest request) {
        return userLoginUseCase.login(request);
    }
}
