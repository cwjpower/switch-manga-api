package com.switchmanga.api.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZipUploadResponse {

    /**
     * 업로드 성공 여부
     */
    private boolean success;

    /**
     * ZIP 파일 저장 경로
     */
    private String zipFilePath;

    /**
     * 압축 해제된 페이지 폴더 경로
     * 예: /uploads/books/20251101123456_abc/pages/
     */
    private String pagesDirectory;

    /**
     * 원본 ZIP 파일명
     */
    private String originalFilename;

    /**
     * ZIP 파일 크기 (bytes)
     */
    private long zipFileSize;

    /**
     * 추출된 파일 개수
     */
    private int extractedFileCount;

    /**
     * 추출된 파일 목록
     */
    private List<String> extractedFiles;

    /**
     * AVF 파일 존재 여부
     */
    private boolean hasAvfFile;

    /**
     * AVF 파일 경로 (존재하는 경우)
     */
    private String avfFilePath;

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
    public static ZipUploadResponse success(String zipFilePath, String pagesDirectory,
                                            String originalFilename, long zipFileSize,
                                            int extractedFileCount, List<String> extractedFiles,
                                            boolean hasAvfFile, String avfFilePath) {
        return ZipUploadResponse.builder()
                .success(true)
                .zipFilePath(zipFilePath)
                .pagesDirectory(pagesDirectory)
                .originalFilename(originalFilename)
                .zipFileSize(zipFileSize)
                .extractedFileCount(extractedFileCount)
                .extractedFiles(extractedFiles)
                .hasAvfFile(hasAvfFile)
                .avfFilePath(avfFilePath)
                .uploadedAt(java.time.LocalDateTime.now().toString())
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static ZipUploadResponse failure(String errorMessage) {
        return ZipUploadResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .uploadedAt(java.time.LocalDateTime.now().toString())
                .build();
    }
}