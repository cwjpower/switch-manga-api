// src/main/java/com/switchmanga/api/dto/series/SeriesListResponse.java

package com.switchmanga.api.dto.series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Series 목록 응답 DTO
 * GET /api/v1/publishers/me/series
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesListResponse {
    
    private Long id;
    
    private String title;
    
    private String author;
    
    private String category;
    
    private String coverImage;
    
    private String status; // ONGOING, COMPLETED
    
    private Boolean active;
    
    private Integer volumeCount;
    
    private LocalDateTime createdAt;
}
