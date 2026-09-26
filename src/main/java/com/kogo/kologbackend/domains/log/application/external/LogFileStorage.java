package com.kogo.kologbackend.domains.log.application.external;

import java.io.InputStream;

public interface LogFileStorage {
    String storeVideo(InputStream stream, String mediaType);
    void deleteVideo(String videoUrl);
}
