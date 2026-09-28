package com.kogo.kologbackend.domains.user.application.usecase.auth;

import com.kogo.kologbackend.domains.user.application.exception.InvalidCredentialsException;
import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.response.UserAuthResponse;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.request.UserLoginRequest;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginUseCase {
    private final UserRepository repository;
    private final AuthTokenProvider authTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserAuthResponse login(UserLoginRequest request) {
        User user = repository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.password())) {
            throw new InvalidCredentialsException();
        }

        UserDetail accessToken = UserDetail.builder().userId(user.id()).build();
        RefreshToken refreshToken = RefreshToken.builder().userId(user.id()).build();

        return UserAuthResponse.builder()
                .accessToken(authTokenProvider.createAccessToken(accessToken))
                .refreshToken(authTokenProvider.createRefreshToken(refreshToken))
                .build();
    }
}
