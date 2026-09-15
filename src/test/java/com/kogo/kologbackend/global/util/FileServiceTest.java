package com.kogo.kologbackend.global.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileServiceTest {

    private static final String SERVER_URL = "https://files.example.test";
    private static final String RESOURCE_URL = SERVER_URL + "/resources/";

    // Encoded fixtures: mathiasbynens/small webp.webp and webm.webm.
    private static final byte[] WEBP = Base64.getDecoder().decode("UklGRhIAAABXRUJQVlA4TAYAAAAvQWxvAGs=");
    private static final byte[] WEBM = Base64.getDecoder().decode(
            "GkXfo0AgQoaBAUL3gQFC8oEEQvOBCEKCQAR3ZWJtQoeBAkKFgQIYU4BnQI0V"
                    + "SalmQCgq17FAAw9CQE2AQAZ3aGFtbXlXQUAGd2hhbW15RIlACECPQAAAAAAA"
                    + "FlSua0AxrkAu14EBY8WBAZyBACK1nEADdW5khkAFVl9WUDglhohAA1ZQOIOB"
                    + "AeBABrCBCLqBCB9DtnVAIueBAKNAHIEAAIAwAQCdASoIAAgAAUAmJaQAA3AA"
                    + "/vz0AAA=");
    // web-platform-tests/wpt media/2x2-green.mp4, blob 157361c2ddc97fc1179006ab1dfa9639fe14950f.
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

    @TempDir
    Path temporaryDirectory;

    private Path uploadDirectory;
    private FileService fileService;
    private TransactionTemplate transactions;

    @BeforeEach
    void setUp() throws IOException {
        uploadDirectory = Files.createDirectory(temporaryDirectory.resolve("uploads"));
        fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "uploadDir", uploadDirectory.toString());
        ReflectionTestUtils.setField(fileService, "serverUrl", SERVER_URL);
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:file-format-" + UUID.randomUUID(), "sa", "");
        transactions = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @ParameterizedTest(name = "Stores detected {0}, ignoring client metadata")
    @MethodSource("acceptedMedia")
    void storesContentWithDetectedExtension(String extension, boolean image, byte[] content) throws IOException {
        MockMultipartFile upload = new MockMultipartFile(
                "file", "../../disguised.html", "text/html", content);

        String storedUrl = transactions.execute(status -> image
                ? fileService.storeImage(upload) : fileService.storeVideo(upload));

        assertTrue(storedUrl.startsWith(RESOURCE_URL));
        String storedName = storedUrl.substring(RESOURCE_URL.length());
        assertTrue(storedName.endsWith("." + extension));
        String identifier = storedName.substring(0, storedName.lastIndexOf('.'));
        assertEquals(identifier, UUID.fromString(identifier).toString());
        Path storedPath = uploadDirectory.resolve(storedName);
        assertTrue(Files.isRegularFile(storedPath));
        assertArrayEquals(content, Files.readAllBytes(storedPath));
        try (Stream<Path> files = Files.list(uploadDirectory)) {
            assertEquals(1, files.count());
        }
    }

    static Stream<Arguments> acceptedMedia() throws IOException {
        return Stream.of(
                Arguments.of("jpg", true, encodedImage("jpg")),
                Arguments.of("png", true, encodedImage("png")),
                Arguments.of("webp", true, WEBP),
                Arguments.of("mp4", false, MP4),
                Arguments.of("webm", false, WEBM));
    }

    @ParameterizedTest(name = "Rejects {0} content as an upload")
    @MethodSource("unsafeMedia")
    void rejectsUnsafeContentDespiteAllowedClientMetadata(String description, byte[] content) throws IOException {
        MockMultipartFile image = new MockMultipartFile("image", "safe.png", "image/png", content);
        MockMultipartFile video = new MockMultipartFile("video", "safe.mp4", "video/mp4", content);

        assertThrows(IllegalArgumentException.class,
                () -> transactions.execute(status -> fileService.storeImage(image)));
        assertThrows(IllegalArgumentException.class,
                () -> transactions.execute(status -> fileService.storeVideo(video)));
        assertUploadDirectoryEmpty();
    }

    static Stream<Arguments> unsafeMedia() {
        return Stream.of(
                Arguments.of("HTML", "<!DOCTYPE html><html><body><script>alert(1)</script></body></html>"
                        .getBytes(StandardCharsets.UTF_8)),
                Arguments.of("SVG", "<svg xmlns=\"http://www.w3.org/2000/svg\"><script>alert(1)</script></svg>"
                        .getBytes(StandardCharsets.UTF_8)));
    }

    @ParameterizedTest(name = "Rejects {0} in the opposite category")
    @MethodSource("acceptedMedia")
    void rejectsCrossCategoryContent(String extension, boolean image, byte[] content) throws IOException {
        MockMultipartFile upload = new MockMultipartFile("file",
                image ? "claimed.mp4" : "claimed.png", image ? "video/mp4" : "image/png", content);

        assertThrows(IllegalArgumentException.class, () -> transactions.execute(status -> image
                ? fileService.storeVideo(upload) : fileService.storeImage(upload)));
        assertUploadDirectoryEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"../", "%2e%2e/", "%2e%2e%2f", "..%2f"})
    void deletionCannotTraverseOutsideUploadDirectory(String traversal) throws IOException {
        String name = UUID.randomUUID() + ".png";
        Path outside = Files.write(temporaryDirectory.resolve(name), WEBP);

        transactions.executeWithoutResult(status -> fileService.deleteAfterCommit(RESOURCE_URL + traversal + name));

        assertTrue(Files.isRegularFile(outside));
        assertArrayEquals(WEBP, Files.readAllBytes(outside));
    }

    @Test
    void deletionCannotFollowSymlinkOutsideUploadDirectory() throws IOException {
        Path outside = Files.write(temporaryDirectory.resolve("outside.png"), WEBP);
        String name = UUID.randomUUID() + ".png";
        Files.createSymbolicLink(uploadDirectory.resolve(name), outside);

        transactions.executeWithoutResult(status -> fileService.deleteAfterCommit(RESOURCE_URL + name));

        assertTrue(Files.isRegularFile(outside));
        assertArrayEquals(WEBP, Files.readAllBytes(outside));
    }

    @Test
    void externalUrlCannotDeleteMatchingLocalFile() throws IOException {
        String name = UUID.randomUUID() + ".png";
        Path local = Files.write(uploadDirectory.resolve(name), WEBP);

        transactions.executeWithoutResult(status -> fileService.deleteAfterCommit(
                "https://untrusted.example.test/resources/" + name));

        assertTrue(Files.isRegularFile(local));
        assertArrayEquals(WEBP, Files.readAllBytes(local));
    }

    private void assertUploadDirectoryEmpty() throws IOException {
        try (Stream<Path> files = Files.list(uploadDirectory)) {
            assertFalse(files.findAny().isPresent());
        }
    }

    private static byte[] encodedImage(String format) throws IOException {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0x00ff00);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, format, output)) {
                throw new IllegalStateException("No ImageIO encoder for " + format);
            }
            return output.toByteArray();
        }
    }
}
