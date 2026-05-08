package com.kogo.kologbackend.application.chat.dto.request;

public record ChatCreateRequest(
        Long logId,
        String chatContent
) {
}
