package com.kogo.kologbackend.domains.log.infrastructure.adapter;

import com.kogo.kologbackend.domains.log.application.exception.InvalidVideoException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.application.external.LogFileStorage;
import com.kogo.kologbackend.global.storage.LocalFileStorageSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
public class VideoFileStorage implements LogFileStorage {
    private final LocalFileStorageSupport storage;

    public VideoFileStorage(@Value("${file.upload-dir}") String directory,
                            @Value("${file.server-url}") String serverUrl) {
        this.storage = new LocalFileStorageSupport(directory, serverUrl, "videos");
    }

    @Override
    public String storeVideo(InputStream stream, String mediaType) {
        String sourceExtension = switch (mediaType) {
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            default -> throw new InvalidVideoException("Unsupported video format: " + mediaType);
        };
        storage.requireActiveTransaction();
        Path directory = storage.directory();
        Path target = storage.allocateTarget(".mp4");
        boolean stored = false;
        Path source = null;
        Throwable failure = null;
        Process process = null;
        try {
            source = Files.createTempFile(directory, "incoming-", sourceExtension);
            Files.copy(stream, source, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            process = new ProcessBuilder("ffmpeg", "-hide_banner", "-loglevel", "error", "-nostdin",
                    "-i", source.toString(), "-map", "0:v:0", "-an", "-sn", "-dn",
                    "-c:v", "libx264", "-pix_fmt", "yuv420p", "-preset", "veryfast", "-crf", "24",
                    "-threads", "2", "-movflags", "+faststart", "-f", "mp4", target.toString())
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            if (!process.waitFor(60, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new VideoUploadException("Video transcoding timed out.");
            }
            if (process.exitValue() != 0 || !Files.isRegularFile(target) || Files.size(target) == 0) {
                throw new InvalidVideoException("The uploaded video cannot be transcoded.");
            }
            stored = true;
        } catch (IOException e) {
            VideoUploadException uploadFailure = new VideoUploadException("Failed to store video.", e);
            failure = uploadFailure;
            throw uploadFailure;
        } catch (InterruptedException e) {
            if (process != null && process.isAlive()) process.destroyForcibly().onExit().join();
            Thread.currentThread().interrupt();
            VideoUploadException uploadFailure = new VideoUploadException("Video transcoding interrupted.", e);
            failure = uploadFailure;
            throw uploadFailure;
        } catch (RuntimeException | Error e) {
            failure = e;
            throw e;
        } finally {
            IOException cleanupFailure = null;
            try {
                if (source != null) Files.deleteIfExists(source);
            } catch (IOException e) {
                cleanupFailure = e;
            }
            if (!stored || cleanupFailure != null) {
                try {
                    Files.deleteIfExists(target);
                } catch (IOException e) {
                    if (cleanupFailure == null) cleanupFailure = e;
                    else cleanupFailure.addSuppressed(e);
                }
            }
            if (cleanupFailure != null) {
                if (failure != null) failure.addSuppressed(cleanupFailure);
                else throw new VideoUploadException("Failed to clean up video.", cleanupFailure);
            }
        }

        storage.deleteOnRollback(target);
        return storage.toPublicUrl(target);
    }

    @Override
    public void deleteVideo(String videoUrl) {
        storage.resolvePublicUrl(videoUrl).ifPresent(storage::deleteAfterCommit);
    }
}
