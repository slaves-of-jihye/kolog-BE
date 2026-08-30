package com.kogo.kologbackend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS 설정.
 *
 * WebMvcConfigurer#addCorsMappings 가 아니라 CorsConfigurationSource 빈으로 등록한다.
 * SecurityFilterChain 의 http.cors() 는 "corsConfigurationSource" 라는 이름의 빈을 먼저 찾고,
 * 없을 때만 MVC 의 mvcHandlerMappingIntrospector 로 폴백한다.
 * 빈으로 직접 등록해두면 CorsFilter 가 인가(authorizeHttpRequests) 앞에서 확실히 동작하므로
 * 프리플라이트(OPTIONS) 요청이 인증 규칙에 걸려 거절되는 일이 없다.
 */
@Configuration
public class WebCorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String[] allowedOrigins
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
