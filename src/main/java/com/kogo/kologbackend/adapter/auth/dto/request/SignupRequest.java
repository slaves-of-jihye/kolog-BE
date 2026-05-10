package com.kogo.kologbackend.adapter.auth.dto.request;

public record SignupRequest(
        String email,
        String password,
        String nickname
) {}