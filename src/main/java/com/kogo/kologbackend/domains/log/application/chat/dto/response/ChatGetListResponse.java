package com.kogo.kologbackend.domains.log.application.chat.dto.response;

public record ChatGetListResponse(
        Long chatId,
        Long userId,
        String nickname,
        String profileImage,
        String chatContent
) {
}
