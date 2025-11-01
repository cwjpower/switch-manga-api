package com.switchmanga.api.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Getter
@Configuration
public class FileUploadConfig {

    @Value("${file.upload.base-dir}")
    private String baseDir;

    @Value("${file.upload.books-dir}")
    private String booksDir;

    @Value("${file.upload.temp-dir}")
    private String tempDir;

    @PostConstruct
    public void init() {
        try {
            // 업로드 디렉토리 생성
            Files.createDirectories(getBaseDirectory());
            Files.createDirectories(getBooksDirectory());
            Files.createDirectories(getTempDirectory());

            log.info("Upload directories initialized:");
            log.info("  - Base: {}", getBaseDirectory());
            log.info("  - Books: {}", getBooksDirectory());
            log.info("  - Temp: {}", getTempDirectory());

        } catch (IOException e) {
            log.error("Failed to create upload directories", e);
            throw new RuntimeException("Failed to initialize upload directories", e);
        }
    }

    public Path getBaseDirectory() {
        return Paths.get(baseDir);
    }

    public Path getBooksDirectory() {
        return Paths.get(baseDir, booksDir);
    }

    public Path getTempDirectory() {
        return Paths.get(baseDir, tempDir);
    }

    /**
     * 새로운 책 업로드 폴더 생성
     * 예: /uploads/books/20251101123456_abc123/
     */
    public Path createBookUploadDirectory() throws IOException {
        String folderName = generateUniqueFolder();
        Path bookDir = getBooksDirectory().resolve(folderName);
        Files.createDirectories(bookDir);
        return bookDir;
    }

    /**
     * 고유한 폴더명 생성 (PHP와 동일한 방식)
     * 예: 20251101123456_abc123
     */
    private String generateUniqueFolder() {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uniqueId;
    }
}