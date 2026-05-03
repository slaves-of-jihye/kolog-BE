package com.kogo.kologbackend.adapter.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH_TOKEN_EXPIRE_TIME);
    }

    private String createToken(Long userId, long expireTime) {
        Date now = new Date();
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
