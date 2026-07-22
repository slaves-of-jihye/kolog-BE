package com.kogo.kologbackend.application.user.internal;

import com.kogo.kologbackend.application.user.dto.response.UserProfileResponse;

public interface UserProfileGetUseCase {
    UserProfileResponse getProfile(Long userId);
}
