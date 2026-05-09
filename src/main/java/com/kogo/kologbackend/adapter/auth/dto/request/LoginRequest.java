package com.kogo.kologbackend.adapter.auth.dto.request;

public record LoginRequest(
        String email,
        String password
) {}
