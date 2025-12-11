package com.switchmanga.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.base-dir}")
    private String uploadBaseDir;

    // ✅ CORS 설정 추가 (Flutter Web에서 이미지 로드 허용)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // 모든 출처 허용 (개발용)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3600);

        // /uploads/** 경로도 CORS 허용
        registry.addMapping("/uploads/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);

        System.out.println("✅ CORS enabled for all origins");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 요청을 실제 파일 시스템 경로로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadBaseDir + "/")
                .setCachePeriod(3600);  // 1시간 캐시

        System.out.println("📂 Static file serving enabled: /uploads/** -> " + uploadBaseDir);
    }
}