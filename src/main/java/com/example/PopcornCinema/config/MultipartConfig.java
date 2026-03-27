package com.example.PopcornCinema.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement(
                "",             // location
                50 * 1024 * 1024,  // maxFileSize = 50MB
                50 * 1024 * 1024,  // maxRequestSize = 50MB
                2 * 1024 * 1024    // fileSizeThreshold = 2MB
        );
    }
}