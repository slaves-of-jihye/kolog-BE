package com.kogo.kologbackend.global.storage;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class LocalFileStorageSupport {
    private final Path directory;
    private final String publicUrlPrefix;

    public LocalFileStorageSupport(String uploadDir, String serverUrl, String category) {
        if (uploadDir == null || uploadDir.isBlank()) {
            throw new IllegalArgumentException("file.upload-dir must be a writable directory.");
        }
        Path base = Path.of(uploadDir).toAbsolutePath().normalize();
        this.directory = base.resolve(category);
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
        this.publicUrlPrefix = url.toString().replaceAll("/+$", "") + "/resources/" + category + "/";
    }

    public Path directory() {
        return directory;
    }

    public Path allocateTarget(String extension) {
        return directory.resolve(UUID.randomUUID() + extension);
    }

    public String toPublicUrl(Path target) {
        return publicUrlPrefix + target.getFileName();
    }

    public void requireActiveTransaction() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("File storage requires an active transaction.");
        }
    }

    public Optional<Path> resolvePublicUrl(String url) {
        if (url == null || !url.startsWith(publicUrlPrefix)) {
            return Optional.empty();
        }
        String filename = url.substring(publicUrlPrefix.length());
        Path target = directory.resolve(filename).normalize();
        if (!directory.equals(target.getParent()) || !target.getFileName().toString().equals(filename)) {
            return Optional.empty();
        }
        return Optional.of(target);
    }

    public void deleteOnRollback(Path target) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        deleteQuietly(target);
                    }
                }
            });
        }
    }

    public void deleteAfterCommit(Path target) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteQuietly(target);
                }
            });
        } else {
            deleteQuietly(target);
        }
    }

    public static void deleteQuietly(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
        }
    }
}
