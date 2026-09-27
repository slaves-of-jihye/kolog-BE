package com.kogo.kologbackend.domains.user.application.external;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;

public interface AuthTokenProvider {
    String createAccessToken(UserDetail token);
    String createRefreshToken(RefreshToken token);
    UserDetail accessTokenUserDetail(String jwt);
    UserDetail refreshTokenUserDetail(String jwt);
}
