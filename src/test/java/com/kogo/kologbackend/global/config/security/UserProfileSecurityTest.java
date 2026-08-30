package com.kogo.kologbackend.global.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GET /api/v1/users/profile 403 이슈 회귀 테스트.
 *
 * MockMvc 대신 실제 톰캣 + JDK HttpClient 로 때린다.
 * CORS 프리플라이트는 서블릿 컨테이너 레벨에서 일어나기 때문에 MockMvc 로는 재현되지 않는다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=18080",
        "app.cors.allowed-origins=http://localhost:3000"
})
class UserProfileSecurityTest {

    private static final String BASE_URL = "http://localhost:18080";
    private static final String PROFILE_URL = BASE_URL + "/api/v1/users/profile";
    private static final String ALLOWED_ORIGIN = "http://localhost:3000";
    private static final String DISALLOWED_ORIGIN = "http://localhost:5173";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static HttpResponse<String> send(HttpRequest request) throws Exception {
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> postJson(String url, String body) throws Exception {
        return send(HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build());
    }

    /** 회원가입(이미 있으면 무시) 후 로그인해서 accessToken 을 얻는다. */
    private String accessToken() throws Exception {
        postJson(BASE_URL + "/api/v1/users/signup",
                "{\"email\":\"profile-test@kolog.com\",\"password\":\"pw12345678\",\"nickname\":\"profileTester\"}");

        HttpResponse<String> login = postJson(BASE_URL + "/api/v1/users/login",
                "{\"email\":\"profile-test@kolog.com\",\"password\":\"pw12345678\"}");
        assertEquals(200, login.statusCode(), "로그인 실패: " + login.body());

        String body = login.body();
        int start = body.indexOf("\"accessToken\":\"") + "\"accessToken\":\"".length();
        int end = body.indexOf('"', start);
        return body.substring(start, end);
    }

    @Test
    @DisplayName("토큰 없이 프로필 조회 -> 403 이 아니라 401")
    void profileWithoutToken() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(PROFILE_URL)).GET().build());

        assertNotEquals(403, response.statusCode(), "인증 없음이 403 으로 새고 있다 (기본 EntryPoint)");
        assertEquals(401, response.statusCode(), "body=" + response.body());
        assertTrue(response.body().contains("\"status\":401"), "ApiResponse 형태가 아님: " + response.body());
    }

    @Test
    @DisplayName("망가진 토큰으로 프로필 조회 -> 401")
    void profileWithInvalidToken() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(PROFILE_URL))
                .header("Authorization", "Bearer not-a-real-jwt")
                .GET().build());

        assertEquals(401, response.statusCode(), "body=" + response.body());
    }

    @Test
    @DisplayName("Bearer 접두어 없는 헤더 -> 500 이 아니라 401")
    void profileWithMalformedHeader() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(PROFILE_URL))
                .header("Authorization", "Bearer")
                .GET().build());

        assertEquals(401, response.statusCode(), "body=" + response.body());
    }

    @Test
    @DisplayName("정상 토큰으로 프로필 조회 -> 200")
    void profileWithValidToken() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(PROFILE_URL))
                .header("Authorization", "Bearer " + accessToken())
                .GET().build());

        assertEquals(200, response.statusCode(), "body=" + response.body());
        assertTrue(response.body().contains("프로필 조회 성공"), "body=" + response.body());
    }

    @Test
    @DisplayName("허용된 Origin 의 프리플라이트(OPTIONS) -> 200 + CORS 헤더")
    void preflightFromAllowedOrigin() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(PROFILE_URL))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", ALLOWED_ORIGIN)
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization")
                .build());

        assertEquals(200, response.statusCode(), "프리플라이트가 인가 규칙에 걸렸다. body=" + response.body());
        assertEquals(ALLOWED_ORIGIN,
                response.headers().firstValue("access-control-allow-origin").orElse(null));
    }

    @Test
    @DisplayName("허용 목록에 없는 Origin -> 403 Invalid CORS request (배포 시 CORS_ALLOWED_ORIGINS 확인용)")
    void requestFromDisallowedOrigin() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(PROFILE_URL))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", DISALLOWED_ORIGIN)
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization")
                .build());

        assertEquals(403, response.statusCode());
        assertTrue(response.body().contains("Invalid CORS request"), "body=" + response.body());
    }

    @Test
    @DisplayName("permitAll 인 GET /api/v1/logs/hours 는 토큰 없이도 200")
    void permitAllEndpointStaysOpen() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(BASE_URL + "/api/v1/logs/hours")).GET().build());

        assertEquals(200, response.statusCode(), "permitAll 엔드포인트가 막혔다. body=" + response.body());
    }

    /**
     * 핸들러가 없는 경로는 톰캣이 /error 로 ERROR 디스패치하고, 그 디스패치도 시큐리티 체인을
     * 다시 탄다. /error 가 permitAll 이 아니면 404 가 AuthenticationEntryPoint 의 401 로
     * 덮여서 나간다. 클라이언트 입장에서는 "URL 오타"가 "토큰 문제"로 보이게 된다.
     */
    @Test
    @DisplayName("없는 경로는 401 이 아니라 404 로 나간다 (/error permitAll 회귀)")
    void unknownPathReturnsNotFound() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(BASE_URL + "/api/v1/logs")).GET().build());

        assertNotEquals(401, response.statusCode(), "404 가 401 로 덮였다. body=" + response.body());
        assertNotEquals(403, response.statusCode(), "404 가 403 으로 덮였다. body=" + response.body());
        assertEquals(404, response.statusCode(), "body=" + response.body());
    }
}
