package com.kogo.kologbackend.domains.user.application.usecase.crud;

import com.kogo.kologbackend.domains.user.application.exception.UserNotFoundException;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.usecase.crud.dto.UserResponse;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGetUseCase {
    private final UserRepository repository;

    public UserResponse get(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.from(user);
    }
}
