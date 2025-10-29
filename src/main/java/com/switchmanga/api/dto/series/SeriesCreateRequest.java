// src/main/java/com/switchmanga/api/dto/series/SeriesCreateRequest.java

package com.switchmanga.api.dto.series;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Series 생성 요청 DTO
 * POST /api/v1/publishers/me/series
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeriesCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author must be less than 100 characters")
    private String author;
    
    @Size(max = 50, message = "Category must be less than 50 characters")
    private String category;
    
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
    
    private String coverImageUrl;
    
    private String status = "ONGOING"; // ONGOING, COMPLETED
    
    private Boolean active = true;
}
