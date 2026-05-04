package com.kogo.kologbackend.adapter.auth.dto.response;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {}