package com.switchmanga.api.dto.volume;

import com.switchmanga.api.entity.Volume;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 권(Volume) 상세 조회 응답 DTO
 * GET /api/v1/publishers/me/volumes/{id}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeDetailResponse {

    private Long id;
    
    private Long seriesId;
    private String seriesTitle;
    
    private Long publisherId;
    private String publisherName;
    
    private Integer volumeNumber;
    private String title;
    private String titleEn;
    private String titleJp;
    private String description;
    
    private Double price;
    private String isbn;
    private LocalDateTime publicationDate;
    
    private String coverImage;
    
    // 파일 정보
    private Integer pageCount;
    
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity → DTO 변환
     */
    public static VolumeDetailResponse from(Volume volume) {
        return VolumeDetailResponse.builder()
                .id(volume.getId())
                .seriesId(volume.getSeries() != null ? volume.getSeries().getId() : null)
                .seriesTitle(volume.getSeries() != null ? volume.getSeries().getTitle() : null)
                .publisherId(volume.getSeries() != null && volume.getSeries().getPublisher() != null 
                        ? volume.getSeries().getPublisher().getId() : null)
                .publisherName(volume.getSeries() != null && volume.getSeries().getPublisher() != null 
                        ? volume.getSeries().getPublisher().getName() : null)
                .volumeNumber(volume.getVolumeNumber())
                .title(volume.getTitle())
                .titleEn(volume.getTitleEn())
                .titleJp(volume.getTitleJp())
                .description(volume.getDescription())
                .price(volume.getPrice())
                .isbn(volume.getIsbn())
                .publicationDate(volume.getPublicationDate())
                .coverImage(volume.getCoverImage())
                .pageCount(volume.getPageCount())
                .active(volume.getActive())
                .createdAt(volume.getCreatedAt())
                .updatedAt(volume.getUpdatedAt())
                .build();
    }
}
