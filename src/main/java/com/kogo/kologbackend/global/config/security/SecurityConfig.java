package com.kogo.kologbackend.global.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CORS 프리플라이트는 CorsFilter 가 먼저 처리하지만, 인가 규칙에서도 열어둔다.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 핸들러가 없는 경로(404)나 처리되지 않은 예외(500)는 서블릿 컨테이너가
                        // /error 로 ERROR 디스패치한다. 이 디스패치도 시큐리티 체인을 다시 타기
                        // 때문에 /error 를 열어두지 않으면 404/500 이 전부 401 로 덮여 나간다.
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/signup", "/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/logs/**", "/api/v1/video/*/chat", "/resources/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/logs/video", "/api/v1/video/emotion", "/api/v1/video/chat").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/logs/*/caption", "/api/v1/users/profile").authenticated()
                        .anyRequest().authenticated()
                )
                // 기본값(Http403ForbiddenEntryPoint)을 쓰면 "인증 없음"도 403 으로 나가서
                // 권한 부족과 구분이 안 된다. 인증 실패는 401, 권한 부족은 403 으로 분리한다.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> SecurityResponseWriter.write(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "인증이 필요합니다. Authorization 헤더를 확인해주세요."
        );
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> SecurityResponseWriter.write(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "접근 권한이 없습니다."
        );
    }
}
