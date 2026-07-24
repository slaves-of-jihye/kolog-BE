package com.kogo.kologbackend.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.server-url}")
    private String serverUrl;

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            String cleanFilename = Path.of(originalFilename).getFileName().toString();
            int extensionIndex = cleanFilename.lastIndexOf(".");
            if (extensionIndex >= 0) {
                extension = cleanFilename.substring(extensionIndex);
            }
        }

        String fileName = UUID.randomUUID() + extension;
        Path uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        Path targetPath = uploadPath.resolve(fileName).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new IllegalArgumentException("잘못된 파일명입니다.");
        }

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }

        return serverUrl.replaceAll("/+$", "") + "/resources/" + fileName;
    }
}
