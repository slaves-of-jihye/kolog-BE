package com.kogo.kologbackend.adapter.auth.provider;

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
public class JwtProvider {

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
        accessSigningKey = signingKey(accessSecret, "jwt.access-secret");
        refreshSigningKey = signingKey(refreshSecret, "jwt.refresh-secret");
        if (accessSecret.equals(refreshSecret)) {
            throw new IllegalStateException("JWT access and refresh secrets must be different.");
        }
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRE_TIME, accessSigningKey);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH_TOKEN_EXPIRE_TIME, refreshSigningKey);
    }

    private String createToken(Long userId, long expireTime, Key key) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(accessSigningKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return Long.parseLong(subject);
    }

    private Key signingKey(String secret, String property) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(property + " must be at least 32 bytes for HS256.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
