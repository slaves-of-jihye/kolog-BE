package com.kogo.kologbackend.domains.user.infrastructure.adapter;

import com.kogo.kologbackend.domains.user.application.exception.InvalidProfileImageException;
import com.kogo.kologbackend.domains.user.application.exception.ProfileImageUploadException;
import com.kogo.kologbackend.domains.user.application.external.UserFileStorage;
import com.kogo.kologbackend.global.storage.LocalFileStorageSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
public class ImageFileStorage implements UserFileStorage {
    private final LocalFileStorageSupport storage;

    public ImageFileStorage(@Value("${file.upload-dir}") String directory,
                             @Value("${file.server-url}") String serverUrl) {
        this.storage = new LocalFileStorageSupport(directory, serverUrl, "images");
    }

    @Override
    public String storeImage(InputStream stream, String mediaType) {
        String sourceExtension = switch (mediaType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> throw new InvalidProfileImageException("Unsupported image format: " + mediaType);
        };
        storage.requireActiveTransaction();
        Path directory = storage.directory();
        Path target = storage.allocateTarget(".jpg");
        boolean stored = false;
        Path source = null;
        Throwable failure = null;
        Process process = null;
        try {
            source = Files.createTempFile(directory, "incoming-", sourceExtension);
            Files.copy(stream, source, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            process = new ProcessBuilder("ffmpeg", "-hide_banner", "-loglevel", "error", "-nostdin",
                    "-i", source.toString(), "-vframes", "1", "-f", "image2", "-c:v", "mjpeg", "-q:v", "3",
                    target.toString())
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new ProfileImageUploadException("Image conversion timed out.");
            }
            if (process.exitValue() != 0 || !Files.isRegularFile(target) || Files.size(target) == 0) {
                throw new InvalidProfileImageException("The uploaded image cannot be converted.");
            }
            stored = true;
        } catch (IOException e) {
            ProfileImageUploadException uploadFailure = new ProfileImageUploadException("Failed to store image.", e);
            failure = uploadFailure;
            throw uploadFailure;
        } catch (InterruptedException e) {
            if (process != null && process.isAlive()) process.destroyForcibly().onExit().join();
            Thread.currentThread().interrupt();
            ProfileImageUploadException uploadFailure =
                    new ProfileImageUploadException("Image conversion interrupted.", e);
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
                else throw new ProfileImageUploadException("Failed to clean up image.", cleanupFailure);
            }
        }

        storage.deleteOnRollback(target);
        return storage.toPublicUrl(target);
    }

    @Override
    public void deleteImage(String imageUrl) {
        storage.resolvePublicUrl(imageUrl).ifPresent(storage::deleteAfterCommit);
    }
}
