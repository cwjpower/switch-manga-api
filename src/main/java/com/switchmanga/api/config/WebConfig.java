package com.switchmanga.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.path:/home/ubuntu/uploads}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 이미지 파일 서빙
        // URL: /uploads/** → 파일 시스템: {uploadPath}/
        // 예: /uploads/books/20251208071039_aedfff40/cover.jpg

        String resourceLocation;

        // OS에 따라 경로 설정
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows 로컬 개발 환경
            resourceLocation = "file:///D:/home/ubuntu/uploads/";
        } else {
            // Linux 서버 환경
            resourceLocation = "file://" + uploadPath + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600); // 1시간 캐시

        System.out.println("📁 Static resource location: " + resourceLocation);
    }
}