package com.kogo.kologbackend.domains.log.application.chat.dto.request;

public record ChatCreateRequest(
        Long logId,
        String chatContent
) {
}
