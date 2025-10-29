// src/main/java/com/switchmanga/api/dto/publisher/PublisherInfoResponse.java

package com.switchmanga.api.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Publisher 정보 응답 DTO
 * GET /api/v1/publishers/me
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherInfoResponse {
    
    private Long id;
    
    private String name;
    
    private String logo;
    
    private String country;
    
    private String email;
    
    private String phone;
    
    private LocalDateTime createdAt;
    
    private Boolean active;
}
