package com.kogo.kologbackend.adapter.dto.response;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {}