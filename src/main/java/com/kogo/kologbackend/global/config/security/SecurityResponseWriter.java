package com.kogo.kologbackend.global.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 시큐리티 필터 단계(= @RestControllerAdvice 가 잡을 수 없는 구간)에서
 * 컨트롤러와 동일한 ApiResponse 형태로 에러를 내려주기 위한 유틸.
 */
final class SecurityResponseWriter {

    private SecurityResponseWriter() {
    }

    static void write(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"status\":" + status + ",\"message\":\"" + escape(message) + "\",\"data\":null}"
        );
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
