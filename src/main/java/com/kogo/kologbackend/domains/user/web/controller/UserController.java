package com.kogo.kologbackend.domains.user.web.controller;

import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserLoginRequest;
import com.kogo.kologbackend.domains.user.application.usecase.auth.UserLoginUseCase;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserSignupRequest;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserAuthResponse;
import com.kogo.kologbackend.domains.user.application.usecase.crud.UserGetUseCase;
import com.kogo.kologbackend.domains.user.application.usecase.crud.UserMeUseCase;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.UserResponse;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.usecase.auth.UserSignupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserSignupUseCase userSignupUseCase;
    private final UserLoginUseCase userLoginUseCase;
    private final UserMeUseCase userMeUseCase;
    private final UserGetUseCase userGetUseCase;

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

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse me(@AuthenticationPrincipal UserDetail userDetail) {
        return userMeUseCase.me(userDetail);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse get(@PathVariable Long userId) {
        return userGetUseCase.get(userId);
    }
}
