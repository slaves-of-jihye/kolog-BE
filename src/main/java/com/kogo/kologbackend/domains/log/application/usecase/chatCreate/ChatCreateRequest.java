package com.kogo.kologbackend.domains.log.application.usecase.chatCreate;

public record ChatCreateRequest(
        Long logId,
        String chatContent
) {
}
