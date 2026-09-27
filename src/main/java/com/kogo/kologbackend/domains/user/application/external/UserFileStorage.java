package com.kogo.kologbackend.domains.user.application.external;

import java.io.InputStream;

public interface UserFileStorage {
    String storeImage(InputStream stream, String mediaType);
    void deleteImage(String imageUrl);
}
