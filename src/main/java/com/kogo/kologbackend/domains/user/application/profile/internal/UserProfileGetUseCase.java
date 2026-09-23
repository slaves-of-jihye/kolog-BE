package com.kogo.kologbackend.domains.user.application.profile.internal;

import com.kogo.kologbackend.domains.user.application.profile.dto.response.UserProfileResponse;

public interface UserProfileGetUseCase {
    UserProfileResponse getProfile(Long userId);
}
