package com.switchmanga.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI switchMangaOpenAPI() {
        // 개발 서버
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        // 운영 서버
        Server prodServer = new Server();
        prodServer.setUrl("http://34.64.84.117:8080");
        prodServer.setDescription("Production Server (GCP)");

        // 연락처 정보
        Contact contact = new Contact();
        contact.setName("Switch Manga Team");
        contact.setEmail("contact@switchmanga.com");

        // 라이선스 정보
        License license = new License()
                .name("Proprietary")
                .url("https://switchmanga.com/license");

        // API 정보
        Info info = new Info()
                .title("Switch Manga API")
                .version("1.0.0")
                .contact(contact)
                .description("디지털 만화/망가 플랫폼을 위한 RESTful API 서버\n\n" +
                        "**주요 기능:**\n" +
                        "- 출판사 관리 (Publisher Management)\n" +
                        "- 시리즈 관리 (Series Management)\n" +
                        "- 볼륨 관리 (Volume Management)\n" +
                        "- 사용자 관리 (User Management)\n" +
                        "- 수익 공유 시스템 (Revenue Sharing: 70% Publisher / 30% Platform)\n\n" +
                        "**기술 스택:**\n" +
                        "- Spring Boot 3.x\n" +
                        "- MariaDB 11\n" +
                        "- Docker\n\n" +
                        "**프로젝트 목표:**\n" +
                        "전 세계 일본 망가 배급을 위한 확장 가능한 디지털 플랫폼 구축")
                .termsOfService("https://switchmanga.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
