package com.kogo.kologbackend.domains.user.infrastructure;

import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.dto.AccessToken;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtAuthTokenProvider implements AuthTokenProvider {

    @Value("${jwt.access-secret}")
    private String accessSecret;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    private Key accessSigningKey;
    private Key refreshSigningKey;

    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24; // 1일
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    void validateSecretKey() {
        if (accessSecret.equals(refreshSecret)) {
            throw new IllegalStateException("JWT access and refresh secrets must be different.");
        }
        accessSigningKey = signingKey(accessSecret, "jwt.access-secret");
        refreshSigningKey = signingKey(refreshSecret, "jwt.refresh-secret");
    }

    public String createAccessToken(AccessToken token) {
        Date now = new Date();
        Claims claims = Jwts.claims()
                .setSubject(token.userId().toString());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(accessSigningKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(RefreshToken token) {
        Date now = new Date();
        Claims claims = Jwts.claims()
                .setSubject(token.userId().toString());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(refreshSigningKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Key signingKey(String secret, String property) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(property + " must be at least 32 bytes for HS256.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
