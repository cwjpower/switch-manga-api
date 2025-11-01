package com.switchmanga.api.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {

    /**
     * 업로드 성공 여부
     */
    private boolean success;

    /**
     * 파일 저장 경로 (상대 경로)
     * 예: /uploads/books/20251101123456_abc123/cover.jpg
     */
    private String filePath;

    /**
     * 원본 파일명
     */
    private String originalFilename;

    /**
     * 저장된 파일명
     */
    private String savedFilename;

    /**
     * 파일 크기 (bytes)
     */
    private long fileSize;

    /**
     * 파일 타입 (MIME type)
     */
    private String contentType;

    /**
     * 에러 메시지 (실패 시)
     */
    private String errorMessage;

    /**
     * 업로드 시간
     */
    private String uploadedAt;

    /**
     * 성공 응답 생성
     */
    public static ImageUploadResponse success(String filePath, String originalFilename,
                                              String savedFilename, long fileSize,
                                              String contentType) {
        return ImageUploadResponse.builder()
                .success(true)
                .filePath(filePath)
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .fileSize(fileSize)
                .contentType(contentType)
                .uploadedAt(java.time.LocalDateTime.now().toString())
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static ImageUploadResponse failure(String errorMessage) {
        return ImageUploadResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .uploadedAt(java.time.LocalDateTime.now().toString())
                .build();
    }
}
