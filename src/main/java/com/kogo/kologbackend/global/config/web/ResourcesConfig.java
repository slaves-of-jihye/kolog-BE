package com.kogo.kologbackend.global.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
    private final Path directory;

    public ResourcesConfig(@Value("${file.upload-dir}") String directory) {
        if (directory == null || directory.isBlank()) {
            throw new IllegalArgumentException("file.upload-dir must be a writable directory.");
        }
        this.directory = Path.of(directory).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = directory.toUri().toString();
        registry.addResourceHandler("/resources/**")
                .addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
