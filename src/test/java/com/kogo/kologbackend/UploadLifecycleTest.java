package com.kogo.kologbackend;

import com.kogo.kologbackend.application.log.dto.request.LogCreateRequest;
import com.kogo.kologbackend.application.log.external.LogRepository;
import com.kogo.kologbackend.application.log.usecase.LogCreateCase;
import com.kogo.kologbackend.application.log.usecase.LogDeleteCase;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.application.user.usecase.UserProfileUpdateCase;
import com.kogo.kologbackend.domain.log.Log;
import com.kogo.kologbackend.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UploadLifecycleTest {

    private static final String SERVER_URL = "http://localhost:8080";
    private static final Path UPLOAD_DIR = temporaryUploadDirectory();
    private static final byte[] PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+aXZkAAAAASUVORK5CYII=");
    // Genuine 2x2 video from web-platform-tests, blob 157361c2ddc97fc1179006ab1dfa9639fe14950f.
    private static final byte[] MP4 = Base64.getMimeDecoder().decode("""
            AAAAIGZ0eXBpc29tAAACAGlzb21pc28yYXZjMW1wNDEAAAAIZnJlZQAACIht
            ZGF0//tQxAADwAABpAAAACAAADSAAAAETEFNRTMuOTkuNVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVUxBTUUzLjk5LjVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVQAAAq4GBf//qtxF6b3m
            2Ui3lizYINkj7u94MjY0IC0gY29yZSAxNDggcjI2NDMgNWM2NTcwNCAtIEgu
            MjY0L01QRUctNCBBVkMgY29kZWMgLSBDb3B5bGVmdCAyMDAzLTIwMTUgLSBo
            dHRwOi8vd3d3LnZpZGVvbGFuLm9yZy94MjY0Lmh0bWwgLSBvcHRpb25zOiBj
            YWJhYz0xIHJlZj0zIGRlYmxvY2s9MTowOjAgYW5hbHlzZT0weDM6MHgxMTMg
            bWU9aGV4IHN1Ym1lPTcgcHN5PTEgcHN5X3JkPTEuMDA6MC4wMCBtaXhlZF9y
            ZWY9MSBtZV9yYW5nZT0xNiBjaHJvbWFfbWU9MSB0cmVsbGlzPTEgOHg4ZGN0
            PTEgY3FtPTAgZGVhZHpvbmU9MjEsMTEgZmFzdF9wc2tpcD0xIGNocm9tYV9x
            cF9vZmZzZXQ9LTIgdGhyZWFkcz0xIGxvb2thaGVhZF90aHJlYWRzPTEgc2xp
            Y2VkX3RocmVhZHM9MCBucj0wIGRlY2ltYXRlPTEgaW50ZXJsYWNlZD0wIGJs
            dXJheV9jb21wYXQ9MCBjb25zdHJhaW5lZF9pbnRyYT0wIGJmcmFtZXM9MyBi
            X3B5cmFtaWQ9MiBiX2FkYXB0PTEgYl9iaWFzPTAgZGlyZWN0PTEgd2VpZ2h0
            Yj0xIG9wZW5fZ29wPTAgd2VpZ2h0cD0yIGtleWludD0yNTAga2V5aW50X21p
            bj0yNSBzY2VuZWN1dD00MCBpbnRyYV9yZWZyZXNoPTAgcmNfbG9va2FoZWFk
            PTQwIHJjPWNyZiBtYnRyZWU9MSBjcmY9MjMuMCBxY29tcD0wLjYwIHFwbWlu
            PTAgcXBtYXg9NjkgcXBzdGVwPTQgaXBfcmF0aW89MS40MCBhcT0xOjEuMDAA
            gAAAABRliIQAK//+2OfzLJOXereQdLvG0f/7UsRdg8AAAaQAAAAgAAA0gAAA
            BFVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVTEFNRTMuOTkuNVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVV//tSxKGDwAABpAAAACAAADSAAAAEVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVMQU1FMy45
            OS41VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVX/+1LEoYPA
            AAGkAAAAIAAANIAAAARVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVUxBTUUzLjk5LjVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVf/7UsShg8AAAaQAAAAgAAA0gAAABFVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVTEFNRTMuOTkuNVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVV//tSxKGDwAABpAAAACAAADSAAAAEVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVX/+1LEoYPAAAGk
            AAAAIAAANIAAAARVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            VVVVVVVVVVVVVVVVVVVVVVVVVQAABP9tb292AAAAbG12aGQAAAAAAAAAAAAA
            AAAAAAPoAAAAtgABAAABAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAEA
            AAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD
            AAACEXRyYWsAAABcdGtoZAAAAAMAAAAAAAAAAAAAAAEAAAAAAAAAtgAAAAAA
            AAAAAAAAAQEAAAAAAQAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAEAA
            AAAAAAAAAAAAAAAAACRlZHRzAAAAHGVsc3QAAAAAAAAAAQAAAJwAAARRAAEA
            AAAAAYltZGlhAAAAIG1kaGQAAAAAAAAAAAAAAAAAAKxEAAAfUVXEAAAAAAAt
            aGRscgAAAAAAAAAAc291bgAAAAAAAAAAAAAAAFNvdW5kSGFuZGxlcgAAAAE0
            bWluZgAAABBzbWhkAAAAAAAAAAAAAAAkZGluZgAAABxkcmVmAAAAAAAAAAEA
            AAAMdXJsIAAAAAEAAAD4c3RibAAAAGBzdHNkAAAAAAAAAAEAAABQbXA0YQAA
            AAAAAAABAAAAAAAAAAAAAgAQAAAAAKxEAAAAAAAsZXNkcwAAAAADgICAGwAB
            AASAgIANaxUAAAAAAPtRAAD7UQaAgIABAgAAACBzdHRzAAAAAAAAAAIAAAAG
            AAAEgAAAAAEAAARRAAAAKHN0c2MAAAAAAAAAAgAAAAEAAAABAAAAAQAAAAIA
            AAAGAAAAAQAAADBzdHN6AAAAAAAAAAAAAAAHAAAA0AAAANEAAADRAAAA0QAA
            ANEAAADRAAAA0QAAABhzdGNvAAAAAAAAAAIAAAAwAAADygAAAhh0cmFrAAAA
            XHRraGQAAAADAAAAAAAAAAAAAAACAAAAAAAAACgAAAAAAAAAAAAAAAAAAAAA
            AAEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAABAAAAAAAIAAAACAAAA
            AAAkZWR0cwAAABxlbHN0AAAAAAAAAAEAAAAoAAAAAAABAAAAAAGQbWRpYQAA
            ACBtZGhkAAAAAAAAAAAAAAAAAAAyAAAAAgBVxAAAAAAALWhkbHIAAAAAAAAA
            AHZpZGUAAAAAAAAAAAAAAABWaWRlb0hhbmRsZXIAAAABO21pbmYAAAAUdm1o
            ZAAAAAEAAAAAAAAAAAAAACRkaW5mAAAAHGRyZWYAAAAAAAAAAQAAAAx1cmwg
            AAAAAQAAAPtzdGJsAAAAl3N0c2QAAAAAAAAAAQAAAIdhdmMxAAAAAAAAAAEA
            AAAAAAAAAAAAAAAAAAAAAAIAAgBIAAAASAAAAAAAAAABAAAAAAAAAAAAAAAA
            AAAAAAAAAAAAAAAAAAAAAAAAAAAAGP//AAAAMWF2Y0MBZAAK/+EAGGdkAAqs
            2V+IiIQAAAMABAAAAwDIPEiWWAEABmjr48siwAAAABhzdHRzAAAAAAAAAAEA
            AAABAAACAAAAABxzdHNjAAAAAAAAAAEAAAABAAAAAQAAAAEAAAAUc3RzegAA
            AAAAAALKAAAAAQAAABRzdGNvAAAAAAAAAAEAAAEAAAAAYnVkdGEAAABabWV0
            YQAAAAAAAAAhaGRscgAAAAAAAAAAbWRpcmFwcGwAAAAAAAAAAAAAAAAtaWxz
            dAAAACWpdG9vAAAAHWRhdGEAAAABAAAAAExhdmY1Ni40MC4xMDE=
            """);

    @Autowired
    private UserRepository users;
    @Autowired
    private LogRepository logs;
    @Autowired
    private UserProfileUpdateCase profiles;
    @Autowired
    private LogCreateCase createLogs;
    @Autowired
    private LogDeleteCase deleteLogs;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private EntityManager entityManager;

    private TransactionTemplate transactions;
    private User owner;
    private Path oldImage;
    private String oldImageUrl;

    @DynamicPropertySource
    static void storageProperties(DynamicPropertyRegistry properties) {
        properties.add("file.upload-dir", () -> UPLOAD_DIR.toString());
        properties.add("file.server-url", () -> SERVER_URL);
        properties.add("spring.datasource.url", () ->
                "jdbc:h2:mem:upload-lifecycle;MODE=PostgreSQL;NON_KEYWORDS=HOUR;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    @BeforeEach
    void seedOwner() throws IOException {
        transactions = new TransactionTemplate(transactionManager);
        oldImage = UPLOAD_DIR.resolve(UUID.randomUUID() + ".png");
        Files.write(oldImage, PNG);
        oldImageUrl = storedUrl(oldImage);
        String unique = UUID.randomUUID().toString();
        User user = User.builder().email(unique + "@upload.test")
                .password("unused-password").nickname(unique).build();
        user.updateProfile(unique, oldImageUrl);
        owner = users.saveAndFlush(user);
    }

    @Test
    void profileCommitReplacesDatabaseUrlAndDeletesOldImageOnlyAfterCommit() throws IOException {
        String newUrl = transactions.execute(status -> {
            String uploaded = profiles.updateProfile(owner.getId(), null, image()).profileImage();
            assertNotEquals(oldImageUrl, uploaded);
            assertTrue(Files.exists(localPath(uploaded)));
            assertTrue(Files.exists(oldImage), "The old image must survive until commit");
            entityManager.flush();
            return uploaded;
        });

        assertEquals(newUrl, persistedProfileUrl());
        assertFalse(Files.exists(oldImage));
        assertArrayEquals(PNG, Files.readAllBytes(localPath(newUrl)));
    }

    @Test
    void profileRollbackRemovesNewImageAndPreservesOldImageAndDatabaseUrl() throws IOException {
        Set<Path> before = storedFiles();
        AtomicReference<String> newUrl = new AtomicReference<>();
        transactions.executeWithoutResult(status -> {
            newUrl.set(profiles.updateProfile(owner.getId(), null, image()).profileImage());
            assertNotEquals(oldImageUrl, newUrl.get());
            assertTrue(Files.exists(localPath(newUrl.get())));
            entityManager.flush();
            status.setRollbackOnly();
        });

        assertEquals(oldImageUrl, persistedProfileUrl());
        assertFalse(Files.exists(localPath(newUrl.get())));
        assertArrayEquals(PNG, Files.readAllBytes(oldImage));
        assertEquals(before, storedFiles());
    }

    @Test
    void duplicateNicknameRejectsReplacementWithoutLeavingAFileOrChangingProfile() throws IOException {
        String takenNickname = UUID.randomUUID().toString();
        users.saveAndFlush(User.builder().email(takenNickname + "@upload.test")
                .password("unused-password").nickname(takenNickname).build());
        Set<Path> before = storedFiles();

        assertThrows(RuntimeException.class,
                () -> profiles.updateProfile(owner.getId(), takenNickname, image()));

        User persisted = users.findById(owner.getId()).orElseThrow();
        assertEquals(owner.getUserInfo().getNickname(), persisted.getUserInfo().getNickname());
        assertEquals(oldImageUrl, persisted.getUserInfo().getProfileImage());
        assertEquals(before, storedFiles());
        assertArrayEquals(PNG, Files.readAllBytes(oldImage));
    }

    @Test
    void logInsertDatabaseFailureRollsBackUploadedVideo() throws IOException {
        Set<Path> before = storedFiles();
        // The real VARCHAR(255) constraint fails only after the video has been stored.
        LogCreateRequest request = new LogCreateRequest(
                new MockMultipartFile("videoFile", "video.mp4", "video/mp4", MP4),
                "x".repeat(256), "2026-09-15", 12);

        assertThrows(DataIntegrityViolationException.class, () -> createLogs.logCreate(owner.getId(), request));

        assertFalse(logs.existsByUserIdAndDateAndHour(owner.getId(), request.date(), request.hour()));
        assertEquals(before, storedFiles());
        assertEquals(oldImageUrl, persistedProfileUrl());
    }

    @Test
    void logDeletionCommitRemovesRecordAndVideoOnlyAfterCommit() throws IOException {
        Log log = seedLog();
        Path video = localPath(log.getVideoUrl());
        transactions.executeWithoutResult(status -> {
            deleteLogs.deleteLog(log.getLogId(), owner.getId());
            entityManager.flush();
            assertTrue(Files.exists(video), "The video must survive until commit");
        });

        assertTrue(logs.findById(log.getLogId()).isEmpty());
        assertFalse(Files.exists(video));
        assertTrue(Files.exists(oldImage), "Unrelated profile images must be retained");
    }

    @Test
    void logDeletionRollbackRetainsRecordAndVideo() throws IOException {
        Log log = seedLog();
        Path video = localPath(log.getVideoUrl());
        Set<Path> before = storedFiles();
        transactions.executeWithoutResult(status -> {
            deleteLogs.deleteLog(log.getLogId(), owner.getId());
            entityManager.flush();
            assertTrue(Files.exists(video));
            status.setRollbackOnly();
        });

        Log persisted = logs.findById(log.getLogId()).orElseThrow();
        assertEquals(log.getVideoUrl(), persisted.getVideoUrl());
        assertArrayEquals(MP4, Files.readAllBytes(video));
        assertEquals(before, storedFiles());
    }

    private Log seedLog() throws IOException {
        Path video = UPLOAD_DIR.resolve(UUID.randomUUID() + ".mp4");
        Files.write(video, MP4);
        return logs.saveAndFlush(Log.builder().user(owner).videoUrl(storedUrl(video))
                .caption("A stored video").date("2026-09-15").hour(12).build());
    }

    private String persistedProfileUrl() {
        return users.findById(owner.getId()).orElseThrow().getUserInfo().getProfileImage();
    }

    private static MockMultipartFile image() {
        return new MockMultipartFile("profileImage", "profile.png", "image/png", PNG);
    }

    private static String storedUrl(Path path) {
        return SERVER_URL + "/resources/" + path.getFileName();
    }

    private static Path localPath(String url) {
        return UPLOAD_DIR.resolve(Path.of(URI.create(url).getPath()).getFileName());
    }

    private static Set<Path> storedFiles() throws IOException {
        try (var files = Files.walk(UPLOAD_DIR)) {
            return files.filter(Files::isRegularFile).collect(Collectors.toSet());
        }
    }

    private static Path temporaryUploadDirectory() {
        try {
            return Files.createTempDirectory("kolog-upload-lifecycle-");
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @AfterAll
    static void removeUploads() throws IOException {
        try (var files = Files.walk(UPLOAD_DIR)) {
            for (Path path : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
