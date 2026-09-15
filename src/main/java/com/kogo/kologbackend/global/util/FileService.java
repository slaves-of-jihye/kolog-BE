package com.kogo.kologbackend.global.util;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp4.MP4Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");
    private static final Map<String, String> VIDEO_EXTENSIONS = Map.of(
            "video/mp4", ".mp4", "video/webm", ".webm");
    private final Tika tika = new Tika();

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.server-url}")
    private String serverUrl;

    public String storeImage(MultipartFile file) {
        return store(file, IMAGE_EXTENSIONS);
    }

    public String storeVideo(MultipartFile file) {
        return store(file, VIDEO_EXTENSIONS);
    }

    private String store(MultipartFile file, Map<String, String> extensions) {
        requireTransaction();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("An upload file is required.");
        }

        Path target = null;
        try (BufferedInputStream input = new BufferedInputStream(file.getInputStream())) {
            // Do not pass the client filename or MIME type to content detection.
            String mediaType = detectMediaType(file, input);
            String extension = extensions.get(mediaType);
            if (extension == null) {
                throw new IllegalArgumentException("Unsupported media format: " + mediaType);
            }
            Path root = Path.of(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(root);
            Path candidate = root.resolve(UUID.randomUUID() + extension);
            Files.createFile(candidate);
            target = candidate;
            Path stored = target;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        deleteQuietly(stored);
                    }
                }
            });
            try (OutputStream output = Files.newOutputStream(target)) {
                input.transferTo(output);
            }
            return resourcePrefix() + target.getFileName();
        } catch (IOException | RuntimeException e) {
            if (target != null) {
                deleteQuietly(target);
            }
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Failed to store upload file.", e);
        }
    }
    private String detectMediaType(MultipartFile file, BufferedInputStream input) throws IOException {
        String mediaType = tika.detect(input);
        if (mediaType.equals("video/quicktime") || mediaType.equals("application/mp4")
                || mediaType.equals("video/mp4")) {
            // ISO media signatures overlap; inspect container brands/tracks, not the filename.
            Metadata metadata = new Metadata();
            metadata.set(Metadata.CONTENT_TYPE, mediaType);
            try (var container = file.getInputStream()) {
                new MP4Parser().parse(container, new DefaultHandler(), metadata, new ParseContext());
            } catch (SAXException | TikaException e) {
                throw new IllegalArgumentException("Invalid MP4 container.", e);
            }
            return metadata.get(Metadata.CONTENT_TYPE);
        }
        return mediaType;
    }


    public void deleteAfterCommit(String storedUrl) {
        requireTransaction();
        if (storedUrl == null || storedUrl.isBlank()) {
            return;
        }
        String prefix = resourcePrefix();
        if (!storedUrl.startsWith(prefix)) {
            log.warn("Skipping file deletion: URL is outside the configured resource base.");
            return;
        }
        String filename = storedUrl.substring(prefix.length());
        if (!filename.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}(\\.[a-zA-Z0-9]+)?")) {
            log.warn("Skipping file deletion: unrecognized stored filename.");
            return;
        }
        Path root = Path.of(uploadDir).toAbsolutePath().normalize();
        Path target = root.resolve(filename).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Invalid stored file path.");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteQuietly(target);
            }
        });
    }

    private String resourcePrefix() {
        return serverUrl.replaceAll("/+$", "") + "/resources/";
    }

    private void requireTransaction() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("File changes require an active database transaction.");
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException | RuntimeException e) {
            // A cleanup failure must not turn an already committed DB change into an HTTP failure.
            log.error("Failed to delete stored file {}", path, e);
        }
    }
}
