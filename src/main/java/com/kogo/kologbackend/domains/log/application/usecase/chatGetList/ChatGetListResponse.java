package com.kogo.kologbackend.domains.log.application.usecase.chatGetList;

public record ChatGetListResponse(
        Long chatId,
        Long userId,
        String nickname,
        String profileImage,
        String chatContent
) {
}
