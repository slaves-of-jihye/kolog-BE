package com.kogo.kologbackend.domains.user.application.usecase.auth;

import com.kogo.kologbackend.domains.user.application.exception.DuplicateEmailException;
import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.request.UserSignupRequest;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.response.UserAuthResponse;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSignupUseCase {
    private final UserRepository repository;
    private final AuthTokenProvider authTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserAuthResponse signup(UserSignupRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();
        User saved = repository.save(user);

        UserDetail accessToken = UserDetail.builder().userId(saved.id()).build();
        RefreshToken refreshToken = RefreshToken.builder().userId(saved.id()).build();

        return UserAuthResponse.builder()
                .accessToken(authTokenProvider.createAccessToken(accessToken))
                .refreshToken(authTokenProvider.createRefreshToken(refreshToken))
                .build();
    }
}
