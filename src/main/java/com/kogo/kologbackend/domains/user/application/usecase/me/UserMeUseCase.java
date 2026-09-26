package com.kogo.kologbackend.domains.user.application.usecase.me;

import com.kogo.kologbackend.domains.user.application.exception.UserNotFoundException;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.usecase.me.dto.UserMeResponse;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMeUseCase {
    private final UserRepository repository;

    public UserMeResponse me(UserDetail userDetail) {
        User user = repository.findById(userDetail.userId())
                .orElseThrow(UserNotFoundException::new);
        return UserMeResponse.builder()
                .id(user.id())
                .nickname(user.nickname())
                .profileImageUrl(user.profileImageUrl())
                .build();
    }
}
