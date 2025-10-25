package com.switchmanga.api.dto.publisher;

import lombok.*;

/**
 * 출판사 통계 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherStatsResponse {

    private Long publisherId;
    private String publisherName;

    // 시리즈/권 통계
    private Long totalSeries;           // 총 시리즈 수
    private Long totalVolumes;          // 총 권 수

    // 매출 통계
    private Double totalRevenue;        // 총 매출
    private Double publisherShare;      // 출판사 수익 (70%)
    private Double platformFee;         // 플랫폼 수수료 (30%)

    // 주문 통계
    private Long totalOrders;           // 총 주문 수
    private Long totalOrderItems;       // 총 주문 항목 수

    // 리뷰 통계
    private Long totalReviews;          // 총 리뷰 수
    private Double averageRating;       // 평균 평점
}