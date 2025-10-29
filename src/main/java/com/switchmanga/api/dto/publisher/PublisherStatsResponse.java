// src/main/java/com/switchmanga/api/dto/publisher/PublisherStatsResponse.java

package com.switchmanga.api.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Publisher 통계 응답 DTO
 * GET /api/v1/publishers/me/stats
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherStatsResponse {
    
    /**
     * 총 시리즈 수
     */
    private Long totalSeries;
    
    /**
     * 총 Volume 수
     */
    private Long totalVolumes;
    
    /**
     * 총 주문 수
     */
    private Long totalOrders;
    
    /**
     * 총 매출
     */
    private Double totalRevenue;
    
    /**
     * 이번 달 매출
     */
    private Double monthlyRevenue;
    
    /**
     * 이번 주 매출
     */
    private Double weeklyRevenue;
    
    /**
     * 오늘 매출
     */
    private Double dailyRevenue;
}
