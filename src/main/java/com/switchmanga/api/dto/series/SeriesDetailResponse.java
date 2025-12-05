package com.switchmanga.api.dto.series;

import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.Volume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesDetailResponse {

    private Long id;
    private String title;
    private String titleEn;
    private String titleJp;
    private String author;
    private String coverImage;
    private String description;
    private String status;

    // ✅ 추가된 필드들 (Series Entity에 있음)
    private Integer totalVolumes;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer viewCount;
    private Long categoryId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Publisher 정보
    private PublisherInfo publisher;

    // Volume 목록
    private List<VolumeInfo> volumes;

    /**
     * Series Entity → SeriesDetailResponse 변환
     */
    public static SeriesDetailResponse from(Series series) {
        return SeriesDetailResponse.builder()
                .id(series.getId())
                .title(series.getTitle())
                .titleEn(series.getTitleEn())
                .titleJp(series.getTitleJp())
                .author(series.getAuthor())
                .coverImage(series.getCoverImage())  // ✅ 대문자 I
                .description(series.getDescription())
                .status(series.getStatus())  // ✅ String 타입

                // ✅ 추가 필드들
                .totalVolumes(series.getTotalVolumes())
                .rating(series.getRating())
                .reviewCount(series.getReviewCount())
                .viewCount(series.getViewCount())
                .categoryId(series.getCategoryId())

                .createdAt(series.getCreatedAt())
                .updatedAt(series.getUpdatedAt())

                // Publisher 정보
                .publisher(series.getPublisher() != null
                        ? PublisherInfo.from(series.getPublisher())
                        : null)

                // Volume 목록
                .volumes(series.getVolumes() != null
                        ? series.getVolumes().stream()
                        .map(VolumeInfo::from)
                        .collect(Collectors.toList())
                        : null)

                .build();
    }

    /**
     * Publisher 간략 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublisherInfo {
        private Long id;
        private String name;
        private String nameEn;
        private String logo;
        private String country;

        public static PublisherInfo from(com.switchmanga.api.entity.Publisher publisher) {
            return PublisherInfo.builder()
                    .id(publisher.getId())
                    .name(publisher.getName())
                    .nameEn(publisher.getNameEn())
                    .logo(publisher.getLogo())
                    .country(publisher.getCountry())
                    .build();
        }
    }

    /**
     * Volume 간략 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolumeInfo {
        private Long id;
        private Integer volumeNumber;
        private String title;
        private String coverImage;
        private BigDecimal price;
        private Integer discountRate;
        private Integer totalPages;
        private Boolean isFree;
        private LocalDateTime createdAt;

        public static VolumeInfo from(Volume volume) {
            return VolumeInfo.builder()
                    .id(volume.getId())
                    .volumeNumber(volume.getVolumeNumber())
                    .title(volume.getTitle())
                    .coverImage(volume.getCoverImage())
                    .price(volume.getPrice())
                    .discountRate(volume.getDiscountRate())
                    .totalPages(volume.getTotalPages())
                    .isFree(volume.getIsFree())
                    .createdAt(volume.getCreatedAt())
                    .build();
        }
    }
}