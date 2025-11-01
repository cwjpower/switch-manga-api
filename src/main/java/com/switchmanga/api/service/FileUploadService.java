package com.switchmanga.api.service;

import com.switchmanga.api.config.FileUploadConfig;
import com.switchmanga.api.dto.upload.ImageUploadResponse;
import com.switchmanga.api.exception.FileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadConfig fileUploadConfig;

    // 허용된 이미지 확장자
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    // 최대 파일 크기 (10MB)
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    /**
     * 표지 이미지 업로드
     */
    public ImageUploadResponse uploadCoverImage(MultipartFile file) {
        try {
            // 1. 파일 검증
            validateImageFile(file);

            // 2. 업로드 폴더 생성
            Path bookDir = fileUploadConfig.createBookUploadDirectory();

            // 3. 파일 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);

            // 4. 저장할 파일명 생성 (cover.jpg)
            String savedFilename = "cover." + extension;
            Path targetPath = bookDir.resolve(savedFilename);

            // 5. 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 6. 상대 경로 생성 (/uploads/books/20251101123456_abc/cover.jpg)
            String relativePath = "/uploads/books/" + bookDir.getFileName() + "/" + savedFilename;

            log.info("Image uploaded successfully: {}", relativePath);

            // 7. 성공 응답 반환
            return ImageUploadResponse.success(
                    relativePath,
                    originalFilename,
                    savedFilename,
                    file.getSize(),
                    file.getContentType()
            );

        } catch (FileValidationException e) {
            log.error("File validation failed: {}", e.getMessage());
            return ImageUploadResponse.failure(e.getMessage());

        } catch (IOException e) {
            log.error("File upload failed", e);
            return ImageUploadResponse.failure("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 이미지 파일 검증
     */
    private void validateImageFile(MultipartFile file) {
        // 1. 파일이 비어있는지 체크
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("파일이 비어있습니다.");
        }

        // 2. 파일 크기 체크
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileValidationException(
                    String.format("파일 크기가 너무 큽니다. (최대: %dMB)", MAX_IMAGE_SIZE / 1024 / 1024)
            );
        }

        // 3. 파일명 체크
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new FileValidationException("파일명이 올바르지 않습니다.");
        }

        // 4. 확장자 체크
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileValidationException(
                    "지원하지 않는 파일 형식입니다. (허용: " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS) + ")"
            );
        }

        // 5. MIME 타입 체크
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileValidationException("이미지 파일만 업로드 가능합니다.");
        }

        log.debug("File validation passed: {}", originalFilename);
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new FileValidationException("파일 확장자가 없습니다.");
        }

        int lastDotIndex = filename.lastIndexOf('.');
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 업로드된 폴더 삭제 (롤백용)
     */
    public void deleteUploadDirectory(Path directory) {
        try {
            if (Files.exists(directory)) {
                Files.walk(directory)
                        .sorted((a, b) -> -a.compareTo(b)) // 역순 정렬 (파일 먼저 삭제)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                log.error("Failed to delete: {}", path, e);
                            }
                        });
                log.info("Directory deleted: {}", directory);
            }
        } catch (IOException e) {
            log.error("Failed to delete directory: {}", directory, e);
        }
    }
}