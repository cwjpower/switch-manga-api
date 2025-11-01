package com.switchmanga.api.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeUploadResponse {

    private boolean success;

    // Volume 정보
    private Long volumeId;
    private String title;
    private Integer volumeNumber;

    // 업로드된 파일 정보
    private String coverImagePath;
    private String pagesDirectory;
    private Integer pageCount;
    private Boolean hasAvfFile;
    private String avfFilePath;

    // 시리즈 정보
    private Long seriesId;
    private String seriesTitle;

    // 에러 정보
    private String errorMessage;

    // 타임스탬프
    private String uploadedAt;

    /**
     * 성공 응답 생성
     */
    public static VolumeUploadResponse success(Long volumeId, String title, Integer volumeNumber,
                                               String coverImagePath, String pagesDirectory,
                                               Integer pageCount, Boolean hasAvfFile, String avfFilePath,
                                               Long seriesId, String seriesTitle) {
        return VolumeUploadResponse.builder()
                .success(true)
                .volumeId(volumeId)
                .title(title)
                .volumeNumber(volumeNumber)
                .coverImagePath(coverImagePath)
                .pagesDirectory(pagesDirectory)
                .pageCount(pageCount)
                .hasAvfFile(hasAvfFile)
                .avfFilePath(avfFilePath)
                .seriesId(seriesId)
                .seriesTitle(seriesTitle)
                .uploadedAt(java.time.LocalDateTime.now().toString())
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static VolumeUploadResponse failure(String errorMessage) {
        return VolumeUploadResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .uploadedAt(java.time.LocalDateTime.now().toString())
                .build();
    }
}