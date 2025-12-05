package com.switchmanga.api.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherStatsResponse {

    private Long totalSeries;
    private Long totalVolumes;
    private Double totalRevenue;  // Double 타입으로 변경
    private Long totalOrders;

    // 추가 통계 (선택)
    private Long totalViews;
    private Long totalReviews;
    private Double averageRating;
}