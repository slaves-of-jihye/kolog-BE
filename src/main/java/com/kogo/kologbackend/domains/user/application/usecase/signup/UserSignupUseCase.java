package com.kogo.kologbackend.domains.user.application.usecase.signup;

import com.kogo.kologbackend.domains.user.application.exception.DuplicateEmailException;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.usecase.JwtProvider;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSignupUseCase {
    private final UserRepository repository;
    private final JwtProvider jwtProvider;

    public UserSignupResponse signup(UserSignupRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = new User(
                null,
                request.email(),
                request.password(),
                request.nickname()
        );
        User saved = repository.save(user);

        String accessToken = jwtProvider.createAccessToken(saved.id());
        String refreshToken = jwtProvider.createRefreshToken(saved.id());

        return new UserSignupResponse(accessToken, refreshToken);
    }
}
