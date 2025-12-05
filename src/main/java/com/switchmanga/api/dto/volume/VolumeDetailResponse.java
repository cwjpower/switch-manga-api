package com.switchmanga.api.dto.volume;

import com.switchmanga.api.entity.Volume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeDetailResponse {

    private Long id;
    private Integer volumeNumber;
    private String title;
    private String titleEn;
    private String titleJp;
    private String coverImage;
    private String description;
    private BigDecimal price;
    private Integer discountRate;
    private Integer totalPages;
    private Long fileSize;
    private LocalDate publishedDate;
    private Boolean isFree;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer purchaseCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Series 정보
    private SeriesInfo series;

    /**
     * Volume Entity → VolumeDetailResponse 변환
     */
    public static VolumeDetailResponse from(Volume volume) {
        return VolumeDetailResponse.builder()
                .id(volume.getId())
                .volumeNumber(volume.getVolumeNumber())
                .title(volume.getTitle())
                .titleEn(volume.getTitleEn())
                .titleJp(volume.getTitleJp())
                .coverImage(volume.getCoverImage())
                .description(volume.getDescription())
                .price(volume.getPrice())
                .discountRate(volume.getDiscountRate())
                .totalPages(volume.getTotalPages())
                .fileSize(volume.getFileSize())
                .publishedDate(volume.getPublishedDate())
                .isFree(volume.getIsFree())
                .rating(volume.getRating())
                .reviewCount(volume.getReviewCount())
                .purchaseCount(volume.getPurchaseCount())
                .viewCount(volume.getViewCount())
                .createdAt(volume.getCreatedAt())
                .updatedAt(volume.getUpdatedAt())

                // Series 정보
                .series(volume.getSeries() != null
                        ? SeriesInfo.from(volume.getSeries())
                        : null)

                .build();
    }

    /**
     * Series 간략 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesInfo {
        private Long id;
        private String title;
        private String titleEn;
        private String author;
        private String coverImage;
        private String status;

        public static SeriesInfo from(com.switchmanga.api.entity.Series series) {
            return SeriesInfo.builder()
                    .id(series.getId())
                    .title(series.getTitle())
                    .titleEn(series.getTitleEn())
                    .author(series.getAuthor())
                    .coverImage(series.getCoverImage())
                    .status(series.getStatus())
                    .build();
        }
    }
}