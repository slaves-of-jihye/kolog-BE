package com.kogo.kologbackend.domains.user.application.usecase.auth;

import com.kogo.kologbackend.domains.user.application.exception.InvalidCredentialsException;
import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.external.dto.AccessToken;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserAuthResponse;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.UserLoginRequest;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginUseCase {
    private final UserRepository repository;
    private final AuthTokenProvider authTokenProvider;

    public UserAuthResponse login(UserLoginRequest request) {
        User user = repository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.password().equals(request.password())) {
            throw new InvalidCredentialsException();
        }

        AccessToken accessToken = new AccessToken(user.id());
        RefreshToken refreshToken = new RefreshToken(user.id());

        return new UserAuthResponse(
                authTokenProvider.createAccessToken(accessToken),
                authTokenProvider.createRefreshToken(refreshToken)
        );
    }
}
