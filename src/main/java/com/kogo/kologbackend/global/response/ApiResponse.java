package com.kogo.kologbackend.global.response;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {}