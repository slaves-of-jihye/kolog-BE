package com.kogo.kologbackend.global.config.security;

import com.kogo.kologbackend.domains.user.infrastructure.adapter.JwtAuthTokenProvider;
import com.kogo.kologbackend.global.config.security.filter.BearerTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthTokenProvider tokens) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/logs").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/logs/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/logs/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/logs/*/comment").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/logs/*/emotion").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(errors -> errors.authenticationEntryPoint(
                        (request, response, exception) -> response.sendError(HttpStatus.UNAUTHORIZED.value())
                    )
                )
                .addFilterBefore(new BearerTokenFilter(tokens), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOriginPatterns(List.of("*"));
        cors.setAllowedMethods(List.of("*"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
