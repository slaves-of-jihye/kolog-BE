package com.kogo.kologbackend.domains.user.web.controller.dto;

import com.kogo.kologbackend.domains.user.application.exception.InvalidProfileUpdateException;
import com.kogo.kologbackend.domains.user.application.exception.ProfileImageUploadException;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.request.UserProfileUpdateRequest;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Builder
public record UserProfileUpdateWebRequest(
        String nickname,
        MultipartFile profileImage
) {
    public UserProfileUpdateRequest toApplication(UserDetail requester) {
        boolean hasNickname = nickname != null && !nickname.isBlank();
        boolean hasProfileImage = profileImage != null && !profileImage.isEmpty();
        if (!hasNickname && !hasProfileImage) {
            throw new InvalidProfileUpdateException("nickname 또는 profileImage 중 하나는 있어야 합니다.");
        }

        try {
            return UserProfileUpdateRequest.builder()
                    .requester(requester)
                    .nickname(hasNickname ? nickname : null)
                    .profileImage(hasProfileImage ? profileImage.getInputStream() : null)
                    .build();
        } catch (IOException e) {
            throw new ProfileImageUploadException("Failed to open the uploaded image.", e);
        }
    }
}
