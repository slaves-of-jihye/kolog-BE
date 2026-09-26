package com.kogo.kologbackend.domains.log.infrastructure;

import com.kogo.kologbackend.domains.log.application.exception.InvalidVideoException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.application.external.LogFileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class VideoFileStorage implements LogFileStorage {
    private final Path directory;
    private final String serverUrl;

    public VideoFileStorage(@Value("${file.upload-dir}") String directory,
                            @Value("${file.server-url}") String serverUrl) {
        if (directory == null || directory.isBlank()) {
            throw new IllegalArgumentException("file.upload-dir must be a writable directory.");
        }
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.directory);
        } catch (IOException e) {
            throw new UncheckedIOException("file.upload-dir cannot be created.", e);
        }
        if (!Files.isDirectory(this.directory) || !Files.isWritable(this.directory)) {
            throw new IllegalArgumentException("file.upload-dir must be a writable directory.");
        }
        if (serverUrl == null || serverUrl.isBlank()) {
            throw new IllegalArgumentException("file.server-url must be an HTTP URL.");
        }
        URI url = URI.create(serverUrl.trim());
        if (!("http".equalsIgnoreCase(url.getScheme()) || "https".equalsIgnoreCase(url.getScheme()))
                || url.getHost() == null || url.getHost().isBlank()
                || url.getRawQuery() != null || url.getRawFragment() != null) {
            throw new IllegalArgumentException("file.server-url must be an HTTP URL.");
        }
        this.serverUrl = url.toString().replaceAll("/+$", "");
    }

    Path directory() {
        return directory;
    }

    @Override
    public String storeVideo(InputStream stream, String mediaType) {
        String sourceExtension = switch (mediaType) {
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            default -> throw new InvalidVideoException("Unsupported video format: " + mediaType);
        };
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Video storage requires an active transaction.");
        }

        Path source = null;
        Path target = directory.resolve(UUID.randomUUID() + ".mp4");
        boolean stored = false;
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

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        Files.deleteIfExists(target);
                    } catch (IOException ignored) {
                        // The database rollback is already complete.
                    }
                }
            }
        });
        return serverUrl + "/resources/" + target.getFileName();
    }
}
