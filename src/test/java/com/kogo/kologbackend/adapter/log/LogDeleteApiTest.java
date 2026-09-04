package com.kogo.kologbackend.adapter.log;

import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.log.Log;
import com.kogo.kologbackend.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DELETE /api/v1/logs/delete/{logId} 회귀 테스트.
 *
 * 핵심은 "남의 로그를 지울 수 있는가" 다. 소유권 검사가 뚫리면 아무나 남의 기록을
 * 삭제할 수 있게 되므로, 회원 두 명을 만들어 교차로 시도한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=18081",
        "app.cors.allowed-origins=http://localhost:3000"
})
class LogDeleteApiTest {

    private static final String BASE_URL = "http://localhost:18081";

    private static final String OWNER_EMAIL = "log-owner@kolog.test";
    private static final String OTHER_EMAIL = "log-other@kolog.test";
    private static final String PASSWORD = "test12345678";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    private Long logId;

    @BeforeEach
    void setUp() throws Exception {
        signUp(OWNER_EMAIL, "logOwner");
        signUp(OTHER_EMAIL, "logOther");

        User owner = userRepository.findByEmail(OWNER_EMAIL).orElseThrow();
        logId = logRepository.save(Log.builder()
                .videoUrl("http://localhost/test-video.mp4")
                .caption("삭제 테스트용")
                .date("2026-09-04")
                .hour(9)
                .user(owner)
                .build()).getLogId();
    }

    @Test
    @DisplayName("토큰 없이 삭제 -> 401")
    void deleteWithoutToken() throws Exception {
        HttpResponse<String> response = send(deleteRequest(logId, null));

        assertEquals(401, response.statusCode(), "body=" + response.body());
        assertTrue(logRepository.findById(logId).isPresent(), "인증 없이 삭제됐다");
    }

    @Test
    @DisplayName("남의 로그 삭제 -> 403 (소유권 검사)")
    void deleteOthersLog() throws Exception {
        HttpResponse<String> response = send(deleteRequest(logId, tokenOf(OTHER_EMAIL)));

        assertEquals(403, response.statusCode(), "남의 로그가 지워졌거나 상태 코드가 틀렸다. body=" + response.body());
        assertTrue(logRepository.findById(logId).isPresent(), "남의 로그가 실제로 삭제됐다");
    }

    @Test
    @DisplayName("없는 로그 삭제 -> 404")
    void deleteMissingLog() throws Exception {
        HttpResponse<String> response = send(deleteRequest(999_999_999L, tokenOf(OWNER_EMAIL)));

        assertEquals(404, response.statusCode(), "body=" + response.body());
    }

    @Test
    @DisplayName("내 로그 삭제 -> 204 이고 실제로 지워진다")
    void deleteOwnLog() throws Exception {
        HttpResponse<String> response = send(deleteRequest(logId, tokenOf(OWNER_EMAIL)));

        assertEquals(204, response.statusCode(), "body=" + response.body());
        assertTrue(logRepository.findById(logId).isEmpty(), "204 인데 DB 에 남아있다");
    }

    private HttpRequest deleteRequest(Long targetLogId, String token) {
        HttpRequest.Builder builder = HttpRequest
                .newBuilder(URI.create(BASE_URL + "/api/v1/logs/delete/" + targetLogId))
                .DELETE();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder.build();
    }

    private static HttpResponse<String> send(HttpRequest request) throws Exception {
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> postJson(String url, String body) throws Exception {
        return send(HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build());
    }

    /** 이미 있으면 400 이 오지만 무시한다. 컨텍스트가 재사용되면 두 번째부터는 중복이다. */
    private static void signUp(String email, String nickname) throws Exception {
        postJson(BASE_URL + "/api/v1/users/signup",
                "{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\",\"nickname\":\"" + nickname + "\"}");
    }

    private static String tokenOf(String email) throws Exception {
        HttpResponse<String> login = postJson(BASE_URL + "/api/v1/users/login",
                "{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}");
        assertEquals(200, login.statusCode(), "로그인 실패: " + login.body());

        String body = login.body();
        int start = body.indexOf("\"accessToken\":\"") + "\"accessToken\":\"".length();
        return body.substring(start, body.indexOf('"', start));
    }
}
