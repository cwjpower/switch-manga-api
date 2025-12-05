package com.switchmanga.api.dto.series;

import com.switchmanga.api.entity.Series;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 시리즈 목록 응답 DTO
 * GET /api/v1/publishers/me/series 응답에 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesListResponse {

    // 기본 정보
    private Long id;
    private String title;
    private String titleEn;        // DB에 없지만 프론트엔드용
    private String titleJp;        // DB에 없지만 프론트엔드용

    // 작가 정보
    private String author;
    private String artist;         // author와 동일하게 처리

    // 출판사 정보
    private Long publisherId;
    private String publisherName;

    // 상태 정보
    private String status;         // ONGOING, COMPLETED, etc.
    private Boolean active;        // 항상 true (DB에 없음)

    // 이미지
    private String coverImage;

    // 날짜
    private LocalDateTime releaseDate;  // createdAt 사용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;    // createdAt과 동일

    // ✅ 추가 필드 (프론트엔드 표시용)
    private Integer volumeCount;   // 권수
    private Double defaultPrice;   // 기본 가격 (0으로 초기화)
    private Integer salesCount;    // 판매량

    /**
     * Entity → DTO 변환
     */
    public static SeriesListResponse from(Series series) {
        if (series == null) {
            return null;
        }

        return SeriesListResponse.builder()
                // 기본 정보
                .id(series.getId())
                .title(series.getTitle())
                .titleEn(series.getTitle())  // 영문명 없으면 title 사용
                .titleJp(null)

                // 작가 정보
                .author(series.getAuthor())
                .artist(series.getAuthor())  // artist 필드 없으면 author 사용

                // 출판사 정보
                .publisherId(series.getPublisher() != null ? series.getPublisher().getId() : null)
                .publisherName(series.getPublisher() != null ? series.getPublisher().getName() : null)

                // 상태 정보
                .status(series.getStatus())
                .active(true)  // DB에 active 없으면 항상 true

                // 이미지
                .coverImage(series.getCoverImage())

                // 날짜
                .releaseDate(series.getCreatedAt())
                .createdAt(series.getCreatedAt())
                .updatedAt(series.getCreatedAt())

                // ✅ 추가 필드
                .volumeCount(0)  // TODO: Volume 카운트 로직 추가
                .defaultPrice(0.0)  // TODO: 가격 정보 추가
                .salesCount(0)  // TODO: 판매량 계산 로직 추가

                .build();
    }
}