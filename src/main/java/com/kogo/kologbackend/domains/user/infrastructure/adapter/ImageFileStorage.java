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
import java.nio.file.StandardCopyOption;

@Component
public class ImageFileStorage implements UserFileStorage {
    private final LocalFileStorageSupport storage;

    public ImageFileStorage(@Value("${file.upload-dir}") String directory,
                             @Value("${file.server-url}") String serverUrl) {
        this.storage = new LocalFileStorageSupport(directory, serverUrl, "images");
    }

    @Override
    public String storeImage(InputStream stream, String mediaType) {
        String extension = switch (mediaType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> throw new InvalidProfileImageException("Unsupported image format: " + mediaType);
        };
        storage.requireActiveTransaction();
        Path target = storage.allocateTarget(extension);
        try {
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ProfileImageUploadException("Failed to store image.", e);
        }
        storage.deleteOnRollback(target);
        return storage.toPublicUrl(target);
    }

    @Override
    public void deleteImage(String imageUrl) {
        storage.resolvePublicUrl(imageUrl).ifPresent(storage::deleteAfterCommit);
    }
}
