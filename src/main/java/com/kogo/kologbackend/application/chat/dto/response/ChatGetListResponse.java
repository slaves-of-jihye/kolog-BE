package com.kogo.kologbackend.application.chat.dto.response;

public record ChatGetListResponse(
        Long chatId,
        Long userId,
        String nickname,
        String profileImage,
        String chatContent
) {
}
