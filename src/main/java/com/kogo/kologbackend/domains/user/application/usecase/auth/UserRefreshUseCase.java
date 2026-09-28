package com.kogo.kologbackend.domains.user.application.usecase.auth;

import com.kogo.kologbackend.domains.user.application.exception.InvalidRefreshTokenException;
import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.request.UserRefreshRequest;
import com.kogo.kologbackend.domains.user.application.usecase.auth.dto.response.UserRefreshResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRefreshUseCase {
    private final UserRepository repository;
    private final AuthTokenProvider authTokenProvider;

    public UserRefreshResponse refresh(UserRefreshRequest request) {
        UserDetail userDetail;
        try {
            userDetail = authTokenProvider.refreshTokenUserDetail(request.refreshToken());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidRefreshTokenException();
        }

        if (repository.findById(userDetail.userId()).isEmpty()) {
            throw new InvalidRefreshTokenException();
        }

        return UserRefreshResponse.builder()
                .accessToken(authTokenProvider.createAccessToken(userDetail))
                .build();
    }
}
