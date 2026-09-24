package com.kogo.kologbackend.domains.user.application.external;

import com.kogo.kologbackend.domains.user.application.external.dto.AccessToken;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;

public interface AuthTokenProvider {
    String createAccessToken(AccessToken token);
    String createRefreshToken(RefreshToken token);
}
