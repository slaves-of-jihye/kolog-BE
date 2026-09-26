package com.kogo.kologbackend.domains.log.web;

import com.kogo.kologbackend.domains.user.application.external.dto.UserDetail;
import com.kogo.kologbackend.domains.user.application.external.dto.RefreshToken;
import com.kogo.kologbackend.domains.user.infrastructure.adapter.JwtAuthTokenProvider;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LogUploadIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JwtAuthTokenProvider tokens;
    @Autowired UserJpaRepository users;
    @TempDir Path temporaryDirectory;

    private static byte[] fixture(String filename) throws IOException {
        try (var stream = LogUploadIntegrationTest.class.getResourceAsStream("/" + filename)) {
            if (stream == null) throw new IllegalStateException("Missing video fixture: " + filename);
            return stream.readAllBytes();
        }
    }

    private void assertSilentH264(byte[] video) throws Exception {
        Path file = temporaryDirectory.resolve("stored.mp4");
        Files.write(file, video);
        Process probe = new ProcessBuilder("ffprobe", "-v", "error", "-show_entries",
                "format=format_name:stream=codec_name,codec_type", "-of", "json", file.toString())
                .redirectErrorStream(true).start();
        var output = probe.getInputStream().readAllBytes();
        assertEquals(0, probe.waitFor(), new String(output));
        var details = JsonMapper.builder().build().readTree(output);
        assertTrue(details.get("format").get("format_name").asText().contains("mp4"));
        assertEquals(1, details.get("streams").size());
        assertEquals("video", details.get("streams").get(0).get("codec_type").asText());
        assertEquals("h264", details.get("streams").get(0).get("codec_name").asText());
    }

    @Test
    @Transactional
    void uploadUsesAccessTokenOwnerAndStoresSilentMp4() throws Exception {
        Long userId = users.save(UserJpaEntity.builder().email("upload@example.org")
                .password("pw").nickname("uploader").build()).getId();
        var response = mvc.perform(multipart("/api/v1/logs/video")
                        .file(new MockMultipartFile("videoFile", "clip.mp4", "video/mp4", fixture("video-with-audio.mp4")))
                        .param("caption", "hello").param("date", "2026-09-25")
                        .param("term", "12").param("uploaderId", "999")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(UserDetail.builder().userId(userId).build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uploaderId").value(userId))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.hour").value(12))
                .andExpect(jsonPath("$.videoUrl").value(org.hamcrest.Matchers.endsWith(".mp4")))
                .andReturn();
        String url = JsonMapper.builder().build()
                .readTree(response.getResponse().getContentAsString()).get("videoUrl").asText();
        String filename = url.substring(url.lastIndexOf('/') + 1);
        assertSilentH264(mvc.perform(get("/resources/" + filename))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray());
    }

    @Test
    @Transactional
    void webmWithAudioBecomesSilentMp4() throws Exception {
        Long userId = users.save(UserJpaEntity.builder().email("webm@example.org")
                .password("pw").nickname("uploader").build()).getId();
        var response = mvc.perform(multipart("/api/v1/logs/video")
                        .file(new MockMultipartFile("videoFile", "clip.webm", "video/webm", fixture("video-with-audio.webm")))
                        .param("caption", "hello").param("date", "2026-09-25").param("term", "12")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(UserDetail.builder().userId(userId).build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.videoUrl").value(org.hamcrest.Matchers.endsWith(".mp4")))
                .andReturn();
        String url = JsonMapper.builder().build()
                .readTree(response.getResponse().getContentAsString()).get("videoUrl").asText();
        assertSilentH264(mvc.perform(get("/resources/" + url.substring(url.lastIndexOf('/') + 1)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray());
    }

    @Test
    void missingInvalidAndRefreshTokensCannotUpload() throws Exception {
        var video = new MockMultipartFile("videoFile", "clip.mp4", "video/mp4", fixture("video-with-audio.mp4"));
        mvc.perform(multipart("/api/v1/logs/video").file(video)).andExpect(status().isUnauthorized());
        mvc.perform(multipart("/api/v1/logs/video").file(video)
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        mvc.perform(multipart("/api/v1/logs/video").file(video)
                        .header("Authorization", "Bearer " + tokens.createRefreshToken(RefreshToken.builder().userId(1L).build())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void rejectsMatroskaWhenDocTypeIsNotWebm() throws Exception {
        Long userId = users.save(UserJpaEntity.builder().email("matroska@example.org")
                .password("pw").nickname("uploader").build()).getId();
        byte[] matroska = HexFormat.of().parseHex(
                "1a45dfa3a34286810142f7810142f2810442f381084282886d6174726f736b614287810442858102");
        mvc.perform(multipart("/api/v1/logs/video")
                        .file(new MockMultipartFile("videoFile", "clip.webm", "video/webm", matroska))
                        .param("caption", "hello").param("date", "2026-09-25").param("term", "12")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(UserDetail.builder().userId(userId).build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void rejectsNonVideoContents() throws Exception {
        Long userId = users.save(UserJpaEntity.builder().email("notvideo@example.org")
                .password("pw").nickname("uploader").build()).getId();
        mvc.perform(multipart("/api/v1/logs/video")
                        .file(new MockMultipartFile("videoFile", "clip.mp4", "video/mp4", "plain text".getBytes()))
                        .param("caption", "hello").param("date", "2026-09-25").param("term", "12")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(UserDetail.builder().userId(userId).build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
    @Test
    @Transactional
    void unreadableMultipartFileReturnsServerError() throws Exception {
        Long userId = users.save(UserJpaEntity.builder().email("open-failure@example.org")
                .password("pw").nickname("uploader").build()).getId();
        var video = new MockMultipartFile("videoFile", "clip.mp4", "video/mp4", new byte[]{1}) {
            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException("Upload temporary file unavailable");
            }
        };
        mvc.perform(multipart("/api/v1/logs/video").file(video)
                        .param("caption", "hello").param("date", "2026-09-25").param("term", "12")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(UserDetail.builder().userId(userId).build())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @Transactional
    void videoReadFailureReturnsServerError() throws Exception {
        Long userId = users.save(UserJpaEntity.builder().email("read-failure@example.org")
                .password("pw").nickname("uploader").build()).getId();
        var video = new MockMultipartFile("videoFile", "clip.mp4", "video/mp4", new byte[]{1}) {
            @Override
            public InputStream getInputStream() {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException("Upload read failed");
                    }
                };
            }
        };
        mvc.perform(multipart("/api/v1/logs/video").file(video)
                        .param("caption", "hello").param("date", "2026-09-25").param("term", "12")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(UserDetail.builder().userId(userId).build())))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void missingLogOwnerReturnsNotFound() throws Exception {
        mvc.perform(multipart("/api/v1/logs/video")
                        .file(new MockMultipartFile("videoFile", "clip.mp4", "video/mp4", fixture("video-with-audio.mp4")))
                        .param("caption", "hello").param("date", "2026-09-25").param("term", "12")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(
                                UserDetail.builder().userId(Long.MAX_VALUE).build())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @Transactional
    void malformedDateReturnsBadRequest() throws Exception {
        Long userId = users.save(UserJpaEntity.builder().email("bad-date@example.org")
                .password("pw").nickname("uploader").build()).getId();
        mvc.perform(multipart("/api/v1/logs/video")
                        .file(new MockMultipartFile("videoFile", "clip.mp4", "video/mp4", fixture("video-with-audio.mp4")))
                        .param("caption", "hello").param("date", "invalid-date").param("term", "12")
                        .header("Authorization", "Bearer " + tokens.createAccessToken(UserDetail.builder().userId(userId).build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

}
