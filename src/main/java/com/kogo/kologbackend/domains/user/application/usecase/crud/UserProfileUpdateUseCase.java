package com.kogo.kologbackend.domains.user.application.usecase.crud;

import com.kogo.kologbackend.domains.user.application.exception.DuplicateNicknameException;
import com.kogo.kologbackend.domains.user.application.exception.ProfileImageUploadException;
import com.kogo.kologbackend.domains.user.application.exception.UserNotFoundException;
import com.kogo.kologbackend.domains.user.application.external.UserFileStorage;
import com.kogo.kologbackend.domains.user.application.external.UserProfileImageValidator;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.UserResponse;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.request.UserProfileUpdateRequest;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserProfileUpdateUseCase {
    private final UserRepository repository;
    private final UserFileStorage files;
    private final UserProfileImageValidator imageValidator;

    @Transactional
    public UserResponse update(UserProfileUpdateRequest request) {
        User user = repository.findById(request.requester().userId())
                .orElseThrow(UserNotFoundException::new);

        String nickname = user.nickname();
        if (request.nickname() != null) {
            if (!request.nickname().equals(user.nickname()) && repository.existsByNickname(request.nickname())) {
                throw new DuplicateNicknameException(request.nickname());
            }
            nickname = request.nickname();
        }

        String profileImageUrl = user.profileImageUrl();
        String previousProfileImageUrl = null;
        if (request.profileImage() != null) {
            try (BufferedInputStream image = new BufferedInputStream(request.profileImage())) {
                String mediaType = imageValidator.detectSupportedMediaType(image);
                profileImageUrl = files.storeImage(image, mediaType);
                previousProfileImageUrl = user.profileImageUrl();
            } catch (IOException e) {
                throw new ProfileImageUploadException("Failed to read or close the uploaded image.", e);
            }
        }

        User updated = repository.save(User.builder()
                .id(user.id())
                .email(user.email())
                .password(user.password())
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build());

        if (previousProfileImageUrl != null) {
            files.deleteImage(previousProfileImageUrl);
        }

        return UserResponse.from(updated);
    }
}
