// src/main/java/com/switchmanga/api/dto/publisher/PublisherUpdateRequest.java

package com.switchmanga.api.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Publisher 정보 수정 요청 DTO
 * PUT /api/v1/publishers/me
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherUpdateRequest {
    
    private String logo;
    
    private String email;
    
    private String phone;
}
