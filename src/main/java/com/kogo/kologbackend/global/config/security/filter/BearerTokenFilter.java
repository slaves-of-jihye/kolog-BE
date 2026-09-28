package com.kogo.kologbackend.global.config.security.filter;

import com.kogo.kologbackend.domains.user.application.external.AuthTokenProvider;
import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class BearerTokenFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";
    private final AuthTokenProvider authTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith(PREFIX)) {
            try {
                UserDetail userDetail = authTokenProvider.accessTokenUserDetail(authorization.substring(PREFIX.length()));
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetail, null, List.of()));
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
