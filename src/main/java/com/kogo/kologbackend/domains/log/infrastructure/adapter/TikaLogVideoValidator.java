package com.kogo.kologbackend.domains.log.infrastructure.adapter;

import com.kogo.kologbackend.domains.log.application.exception.InvalidVideoException;
import com.kogo.kologbackend.domains.log.application.external.LogVideoValidator;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class TikaLogVideoValidator implements LogVideoValidator {
    @Override
    public String detectSupportedMediaType(BufferedInputStream video) throws IOException {
        video.mark(65536);
        String mediaType = new Tika().detect(video);
        video.reset();
        if (mediaType.equals("video/quicktime")) {
            video.mark(12);
            byte[] header = video.readNBytes(12);
            video.reset();
            if (header.length == 12 && new String(header, 4, 4, StandardCharsets.US_ASCII).equals("ftyp")
                    && !new String(header, 8, 4, StandardCharsets.US_ASCII).equals("qt  ")) {
                mediaType = "video/mp4";
            }
        }
        if (mediaType.equals("application/x-matroska")) {
            video.mark(4096);
            byte[] header = video.readNBytes(4096);
            video.reset();
            byte[] webmDocType = {0x42, (byte) 0x82, (byte) 0x84, 'w', 'e', 'b', 'm'};
            for (int i = 4; i <= header.length - webmDocType.length; i++) {
                int j = 0;
                while (j < webmDocType.length && header[i + j] == webmDocType[j]) {
                    j++;
                }
                if (j == webmDocType.length) {
                    mediaType = "video/webm";
                    break;
                }
            }
        }
        if (!mediaType.equals("video/mp4") && !mediaType.equals("video/webm")) {
            throw new InvalidVideoException("Unsupported video format: " + mediaType);
        }
        return mediaType;
    }
}
