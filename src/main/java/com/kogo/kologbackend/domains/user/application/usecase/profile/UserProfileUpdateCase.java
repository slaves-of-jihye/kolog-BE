package com.kogo.kologbackend.domains.user.application.usecase.profile;

import com.kogo.kologbackend.domains.user.infrastructure.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.UserJpaRepository;
import com.kogo.kologbackend.global.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileUpdateCase {
    private final UserJpaRepository userRepository;
    private final FileService fileService;

    @Transactional
    public UserProfileResponse updateProfile(Long userId, String nickname, MultipartFile profileImage) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        String targetNickname = user.getNickname();
        if (nickname != null && !nickname.isBlank()) {
            if (!nickname.equals(targetNickname) && userRepository.existsByNickname(nickname)) {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }
            targetNickname = nickname;
        }

        String imageUrl = user.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            String previousImageUrl = imageUrl;
            imageUrl = fileService.storeImage(profileImage);
            fileService.deleteAfterCommit(previousImageUrl);
        }

        user.updateProfile(targetNickname, imageUrl);

        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImage(),
                user.getEmail(),
                LocalDateTime.now().toString()
        );
    }
}
