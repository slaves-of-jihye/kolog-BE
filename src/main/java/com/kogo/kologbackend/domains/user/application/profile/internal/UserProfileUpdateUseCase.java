package com.kogo.kologbackend.domains.user.application.profile.internal;

import com.kogo.kologbackend.domains.user.application.profile.dto.response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileUpdateUseCase {
    UserProfileResponse updateProfile(Long userId, String nickname, MultipartFile profileImage);
}
