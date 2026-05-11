package com.kogo.kologbackend.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.server-url}")
    private String serverUrl;

    public String storeFile(MultipartFile file) {
        if (file.isEmpty() || file == null) {
            return null;
        }

        File directory = new File(uploadDir);
        if(!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File targetFile = new File(uploadDir + fileName);

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }

        return serverUrl + "/resources/" + fileName;
    }
}
