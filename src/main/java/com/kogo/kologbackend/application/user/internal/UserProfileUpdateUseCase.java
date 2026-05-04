package com.kogo.kologbackend.application.user.internal;

import com.kogo.kologbackend.application.user.dto.response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileUpdateUseCase {
    UserProfileResponse updateProfile(Long userId, String nickname, MultipartFile profileImage);
}
