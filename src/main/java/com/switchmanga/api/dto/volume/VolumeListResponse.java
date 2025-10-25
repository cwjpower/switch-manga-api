package com.switchmanga.api.dto.volume;

import com.switchmanga.api.entity.Volume;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 권(Volume) 목록 조회 응답 DTO
 * GET /api/v1/publishers/me/volumes
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeListResponse {

    private Long id;
    private Long seriesId;
    private String seriesTitle;
    
    private Integer volumeNumber;
    private String title;
    private String titleEn;
    private String titleJp;
    
    private String coverImage;
    
    private Double price;
    private String isbn;
    private LocalDateTime publicationDate;
    
    private Integer pageCount;
    private Boolean active;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity → DTO 변환
     */
    public static VolumeListResponse from(Volume volume) {
        return VolumeListResponse.builder()
                .id(volume.getId())
                .seriesId(volume.getSeries() != null ? volume.getSeries().getId() : null)
                .seriesTitle(volume.getSeries() != null ? volume.getSeries().getTitle() : null)
                .volumeNumber(volume.getVolumeNumber())
                .title(volume.getTitle())
                .titleEn(volume.getTitleEn())
                .titleJp(volume.getTitleJp())
                .coverImage(volume.getCoverImage())
                .price(volume.getPrice())
                .isbn(volume.getIsbn())
                .publicationDate(volume.getPublicationDate())
                .pageCount(volume.getPageCount())
                .active(volume.getActive())
                .createdAt(volume.getCreatedAt())
                .updatedAt(volume.getUpdatedAt())
                .build();
    }
}
