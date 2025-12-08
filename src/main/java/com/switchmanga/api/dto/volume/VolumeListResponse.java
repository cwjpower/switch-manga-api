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
public class VolumeListResponse {

    private Long id;
    private Integer volumeNumber;
    private String title;
    private String titleEn;
    private String titleJp;
    private String description;  // ✅ 추가!
    private String coverImage;
    private BigDecimal price;
    private Integer discountRate;
    private Integer totalPages;
    private LocalDate publishedDate;
    private Boolean isFree;
    private String status;  // ✅ 추가!
    private Integer freePages;  // ✅ 추가!
    private String zipFile;  // ✅ ZIP 파일명
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer purchaseCount;
    private Integer viewCount;
    private LocalDateTime createdAt;

    // Series 정보
    private Long seriesId;
    private String seriesTitle;

    /**
     * Volume Entity → VolumeListResponse 변환
     */
    public static VolumeListResponse from(Volume volume) {
        return VolumeListResponse.builder()
                .id(volume.getId())
                .volumeNumber(volume.getVolumeNumber())
                .title(volume.getTitle())
                .titleEn(volume.getTitleEn())
                .titleJp(volume.getTitleJp())
                .description(volume.getDescription())  // ✅ 추가!
                .coverImage(volume.getCoverImage())
                .price(volume.getPrice())
                .discountRate(volume.getDiscountRate())
                .totalPages(volume.getTotalPages())
                .publishedDate(volume.getPublishedDate())
                .isFree(volume.getIsFree())
                .status(volume.getStatus())  // ✅ 추가!
                .freePages(volume.getFreePages())  // ✅ 추가!
                .zipFile(volume.getZipFile())  // ✅ ZIP 파일명
                .rating(volume.getRating())
                .reviewCount(volume.getReviewCount())
                .purchaseCount(volume.getPurchaseCount())
                .viewCount(volume.getViewCount())
                .createdAt(volume.getCreatedAt())

                // Series 정보
                .seriesId(volume.getSeries() != null
                        ? volume.getSeries().getId()
                        : null)
                .seriesTitle(volume.getSeries() != null
                        ? volume.getSeries().getTitle()
                        : null)

                .build();
    }
}