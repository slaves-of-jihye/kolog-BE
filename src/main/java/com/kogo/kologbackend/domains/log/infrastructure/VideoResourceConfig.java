package com.kogo.kologbackend.domains.log.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
@Configuration
public class VideoResourceConfig implements WebMvcConfigurer {
    private final Path directory;

    public VideoResourceConfig(VideoFileStorage storage) {
        this.directory = storage.directory();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = directory.toUri().toString();
        registry.addResourceHandler("/resources/**")
                .addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
