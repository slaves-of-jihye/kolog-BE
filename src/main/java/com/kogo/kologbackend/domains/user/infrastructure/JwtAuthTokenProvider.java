package com.kogo.kologbackend.domains.user.infrastructure;

import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtAuthTokenProvider implements AuthTokenProvider {

    @Value("${jwt.secret.access-token}")
    private String accessSecret;
    @Value("${jwt.secret.refresh-token}")
    private String refreshSecret;

    private SecretKey accessSigningKey;
    private SecretKey refreshSigningKey;

    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24; // 1일
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    void validateSecretKey() {
        if (accessSecret == null || accessSecret.isBlank()
                || refreshSecret == null || refreshSecret.isBlank()) {
            throw new IllegalStateException("JWT access and refresh secrets must be set.");
        }
        if (accessSecret.equals(refreshSecret)) {
            throw new IllegalStateException("JWT access and refresh secrets must be different.");
        }
        accessSigningKey = createSecretKey(accessSecret);
        refreshSigningKey = createSecretKey(refreshSecret);
    }

    public String createAccessToken(UserDetail token) {
        Date now = new Date();
        Map<String, Object> claims = Map.of(
                "sub", token.userId().toString()
        );

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(accessSigningKey, Jwts.SIG.HS256)
                .compact();
    }

    public String createRefreshToken(RefreshToken token) {
        Date now = new Date();
        Map<String, Object> claims = Map.of(
                "sub", token.userId().toString()
        );

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(refreshSigningKey, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public UserDetail accessTokenUserDetail(String jwt) {
        Claims claims = Jwts.parser()
                .verifyWith(accessSigningKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        return UserDetail.builder()
                .userId(Long.valueOf(claims.getSubject()))
                .build();
    }


    private SecretKey createSecretKey(String secret) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secrets must be at least 32 UTF-8 bytes.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
