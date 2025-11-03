package com.switchmanga.api.dto.series;

import com.switchmanga.api.entity.Series;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import java.time.LocalDateTime;

/**
 * 시리즈 상세 조회 응답 DTO
 * GET /api/v1/publishers/me/series/{id}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesDetailResponse {

    private Long id;
    private String title;
    private String titleEn;
    private String titleJp;
    private String description;
    private String author;
    private String artist;
    
    private Long publisherId;
    private String publisherName;
    
    private String status;
    private String coverImage;
    private LocalDate releaseDate;
    
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity → DTO 변환
     */
    public static SeriesDetailResponse from(Series series) {
        return SeriesDetailResponse.builder()
                .id(series.getId())
                .title(series.getTitle())
                .titleEn(series.getTitleEn())
                .titleJp(series.getTitleJp())
                .description(series.getDescription())
                .author(series.getAuthor())
                .artist(series.getArtist())
                .publisherId(series.getPublisher() != null ? series.getPublisher().getId() : null)
                .publisherName(series.getPublisher() != null ? series.getPublisher().getName() : null)
                .status(series.getStatus().name())
                .coverImage(series.getCoverImage())
                .releaseDate(series.getReleaseDate())
                .active(series.getActive())
                .createdAt(series.getCreatedAt())
                .updatedAt(series.getUpdatedAt())
                .build();
    }
}
