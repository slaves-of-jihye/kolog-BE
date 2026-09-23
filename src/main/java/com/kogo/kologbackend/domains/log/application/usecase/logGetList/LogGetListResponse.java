package com.kogo.kologbackend.domains.log.application.usecase.logGetList;

public record LogGetListResponse(
        String date,
        Integer hour,
        Long userId,
        String nickname,
        String profileImage,
        String videoUrl,
        String caption
) {

}
