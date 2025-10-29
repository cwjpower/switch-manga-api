// src/main/java/com/switchmanga/api/dto/dashboard/DashboardResponse.java

package com.switchmanga.api.dto.dashboard;

import com.switchmanga.api.dto.series.SeriesListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dashboard 종합 데이터 응답 DTO
 * GET /api/v1/publishers/me/dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    // ========================================
    // 1. Publisher 기본 정보
    // ========================================
    private Long publisherId;
    private String publisherName;
    private String publisherLogo;
    
    // ========================================
    // 2. 통계 데이터
    // ========================================
    private Long totalSeries;
    private Long totalVolumes;
    private Long totalOrders;
    private Double totalRevenue;
    
    // ========================================
    // 3. 최근 매출 (기간별)
    // ========================================
    private Double todayRevenue;
    private Double weekRevenue;
    private Double monthRevenue;
    
    // ========================================
    // 4. 최근 주문
    // ========================================
    private Long todayOrders;
    private Long weekOrders;
    private Long monthOrders;
    
    // ========================================
    // 5. 인기 Series Top 5
    // ========================================
    private List<TopSeries> topSeries;
    
    // ========================================
    // 6. 최근 등록된 Series
    // ========================================
    private List<SeriesListResponse> recentSeries;
    
    /**
     * Top Series DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSeries {
        private Long seriesId;
        private String title;
        private String author;
        private String coverImage;
        private Integer volumeCount;
        private Double revenue;
        private Long orderCount;
    }
}
