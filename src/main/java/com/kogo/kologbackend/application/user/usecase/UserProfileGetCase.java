package com.kogo.kologbackend.application.user.usecase;

import com.kogo.kologbackend.application.user.dto.response.UserProfileResponse;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaRepository;
import com.kogo.kologbackend.application.user.internal.UserProfileGetUseCase;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileGetCase implements UserProfileGetUseCase {
    private final UserJpaRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImage(),
                user.getEmail(),
                null
        );
    }
}
