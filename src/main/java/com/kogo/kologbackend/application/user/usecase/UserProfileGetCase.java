package com.kogo.kologbackend.application.user.usecase;

import com.kogo.kologbackend.application.user.dto.response.UserProfileResponse;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.application.user.internal.UserProfileGetUseCase;
import com.kogo.kologbackend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileGetCase implements UserProfileGetUseCase {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return new UserProfileResponse(
                user.getId(),
                user.getUserInfo().getNickname(),
                user.getUserInfo().getProfileImage(),
                user.getEmail(),
                null
        );
    }
}
