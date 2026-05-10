package com.kogo.kologbackend.application.user.usecase;

import com.kogo.kologbackend.application.user.dto.response.UserProfileResponse;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.application.user.internal.UserProfileUpdateUseCase;
import com.kogo.kologbackend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileUpdateCase implements UserProfileUpdateUseCase {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, String nickname, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        String imageUrl = user.getUserInfo().getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            imageUrl = "https://... 나중에 변경할 저장소 url" + profileImage.getOriginalFilename();
        }

        String targetNickname = user.getUserInfo().getNickname();
        if (nickname != null && !nickname.isBlank()) {
            if (!nickname.equals(targetNickname) && userRepository.existsByNickname(nickname)) {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }
            targetNickname = nickname;
        }

        user.updateProfile(targetNickname, imageUrl);

        return new UserProfileResponse(
                user.getId(),
                user.getUserInfo().getNickname(),
                user.getUserInfo().getProfileImage(),
                user.getEmail(),
                LocalDateTime.now().toString()
        );
    }
}
