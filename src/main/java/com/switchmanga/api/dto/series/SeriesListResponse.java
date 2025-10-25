package com.switchmanga.api.dto.series;

import com.switchmanga.api.entity.Series;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 시리즈 목록 조회 응답 DTO
 * GET /api/v1/publishers/me/series
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesListResponse {

    private Long id;
    private String title;
    private String titleEn;
    private String titleJp;
    private String author;
    private String artist;              // illustrator → artist
    
    private Long publisherId;
    private String publisherName;
    
    private String status;              // ONGOING, COMPLETED, HIATUS
    private String coverImage;
    private LocalDateTime releaseDate;
    private Boolean active;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity → DTO 변환
     */
    public static SeriesListResponse from(Series series) {
        return SeriesListResponse.builder()
                .id(series.getId())
                .title(series.getTitle())
                .titleEn(series.getTitleEn())
                .titleJp(series.getTitleJp())
                .author(series.getAuthor())
                .artist(series.getArtist())
                .publisherId(series.getPublisher() != null ? series.getPublisher().getId() : null)
                .publisherName(series.getPublisher() != null ? series.getPublisher().getName() : null)
                .status(series.getStatus())
                .coverImage(series.getCoverImage())
                .releaseDate(series.getReleaseDate())
                .active(series.getActive())
                .createdAt(series.getCreatedAt())
                .updatedAt(series.getUpdatedAt())
                .build();
    }
}
