package com.kogo.kologbackend.global.config.security;

import com.kogo.kologbackend.adapter.auth.provider.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 없으면 익명으로 통과시킨다. permitAll 엔드포인트는 그대로 동작하고,
        // 인증이 필요한 엔드포인트는 뒤쪽 인가 단계에서 401 로 거절된다.
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            SecurityResponseWriter.write(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization 헤더 형식이 올바르지 않습니다. 'Bearer {token}' 형태여야 합니다."
            );
            return;
        }

        try {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            Long userId = jwtProvider.getUserIdFromToken(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            SecurityResponseWriter.write(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "유효하지 않거나 만료된 토큰입니다."
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
