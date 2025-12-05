package com.switchmanga.api.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeUploadResponse {

    private Long volumeId;
    private Long seriesId;
    private Integer volumeNumber;
    private String title;
    private Integer pageCount;
    private String coverImage;
    private String message;

    // ✅ 추가 필드
    private boolean success;
    private String errorMessage;

    // ✅ isSuccess() 메서드 - Lombok @Getter가 자동 생성하지만 명시적으로 추가
    public boolean isSuccess() {
        return success;
    }

    // ========================================
    // 팩토리 메서드
    // ========================================

    /**
     * 성공 응답 생성
     */
    public static VolumeUploadResponse success(Long volumeId, Long seriesId,
                                               Integer volumeNumber, String title, Integer pageCount, String coverImage) {
        return VolumeUploadResponse.builder()
                .volumeId(volumeId)
                .seriesId(seriesId)
                .volumeNumber(volumeNumber)
                .title(title)
                .pageCount(pageCount)
                .coverImage(coverImage)
                .message("볼륨 업로드 완료")
                .success(true)
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static VolumeUploadResponse failure(String errorMessage) {
        return VolumeUploadResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .message("볼륨 업로드 실패")
                .build();
    }
}