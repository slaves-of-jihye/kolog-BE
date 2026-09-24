package com.kogo.kologbackend.domains.user.application.usecase.auth;

import com.kogo.kologbackend.domains.user.application.exception.DuplicateEmailException;
import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.external.dto.AccessToken;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserSignupRequest;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserAuthResponse;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSignupUseCase {
    private final UserRepository repository;
    private final AuthTokenProvider authTokenProvider;

    public UserAuthResponse signup(UserSignupRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = new User(
                null,
                request.email(),
                request.password(),
                request.nickname(),
                null
        );
        User saved = repository.save(user);

        AccessToken accessToken = new AccessToken(saved.id());
        RefreshToken refreshToken = new RefreshToken(saved.id());

        return new UserAuthResponse(
                authTokenProvider.createAccessToken(accessToken),
                authTokenProvider.createRefreshToken(refreshToken)
        );
    }
}
