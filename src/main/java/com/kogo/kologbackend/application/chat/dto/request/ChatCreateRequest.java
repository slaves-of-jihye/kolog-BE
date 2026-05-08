package com.kogo.kologbackend.application.chat.dto.request;

public record ChatCreateRequest(
        Long userId,
        Long logId,
        String chatContent
) {
}
