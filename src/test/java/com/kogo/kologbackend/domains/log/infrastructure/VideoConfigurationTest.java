package com.kogo.kologbackend.domains.log.infrastructure;

import com.kogo.kologbackend.domains.user.infrastructure.JwtAuthTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class VideoConfigurationTest {
    @TempDir Path directory;

    private ApplicationContextRunner context(String uploadDir, String serverUrl) {
        return new ApplicationContextRunner()
                .withUserConfiguration(VideoFileStorage.class, VideoResourceConfig.class, JwtAuthTokenProvider.class)
                .withPropertyValues("file.upload-dir=" + uploadDir, "file.server-url=" + serverUrl,
                        "jwt.secret.access-token=access-token-secret-with-at-least-32-bytes",
                        "jwt.secret.refresh-token=refresh-token-secret-with-at-least-32-bytes");
    }

    @Test
    void validSettingsAllowStartup() {
        context(directory.toString(), "http://localhost:8080")
                .run(application -> assertNull(application.getStartupFailure()));
    }

    @Test
    void blankOrNonDirectoryUploadPathPreventsStartup() throws Exception {
        context(" ", "http://localhost:8080")
                .run(application -> assertNotNull(application.getStartupFailure()));
        Path file = Files.writeString(directory.resolve("file"), "not a directory");
        context(file.toString(), "http://localhost:8080")
                .run(application -> assertNotNull(application.getStartupFailure()));
    }

    @Test
    void missingOrMalformedServerUrlPreventsStartup() {
        context(directory.toString(), " ")
                .run(application -> assertNotNull(application.getStartupFailure()));
        context(directory.toString(), "not-a-url")
                .run(application -> assertNotNull(application.getStartupFailure()));
    }

    @Test
    void invalidJwtSecretsPreventStartup() {
        context(directory.toString(), "http://localhost:8080")
                .withPropertyValues("jwt.secret.access-token=short")
                .run(application -> assertNotNull(application.getStartupFailure()));
        context(directory.toString(), "http://localhost:8080")
                .withPropertyValues("jwt.secret.refresh-token=access-token-secret-with-at-least-32-bytes")
                .run(application -> assertNotNull(application.getStartupFailure()));
    }

}
