package com.kogo.kologbackend.domains.user.infrastructure.adapter;

import com.kogo.kologbackend.domains.user.application.exception.InvalidProfileImageException;
import com.kogo.kologbackend.domains.user.application.external.UserProfileImageValidator;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Set;

@Component
public class TikaUserProfileImageValidator implements UserProfileImageValidator {
    private static final Set<String> SUPPORTED = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    @Override
    public String detectSupportedMediaType(BufferedInputStream image) throws IOException {
        image.mark(65536);
        String mediaType = new Tika().detect(image);
        image.reset();
        if (!SUPPORTED.contains(mediaType)) {
            throw new InvalidProfileImageException("Unsupported image format: " + mediaType);
        }
        return mediaType;
    }
}
