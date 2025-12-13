package com.switchmanga.api.dto.review;

import java.util.Map;

/**
 * 출판사용 리뷰 통계 응답 DTO
 */
public class PublisherReviewStatsResponse {

    // 평균 별점
    private Double averageRating;

    // 총 리뷰 수
    private Long totalReviews;

    // 이번 달 리뷰 수
    private Long thisMonthReviews;

    // 미답변 리뷰 수
    private Long unansweredReviews;

    // 베스트 리뷰 수
    private Long bestReviewCount;

    // 별점 분포 (1~5)
    private Map<Integer, Long> ratingDistribution;

    // 최근 추이
    private TrendInfo recentTrend;

    // ==================== Inner Class: 추이 정보 ====================
    public static class TrendInfo {
        private Long thisMonth;
        private Long lastMonth;
        private String changeRate;

        public TrendInfo() {}

        public TrendInfo(Long thisMonth, Long lastMonth) {
            this.thisMonth = thisMonth;
            this.lastMonth = lastMonth;
            if (lastMonth != null && lastMonth > 0) {
                double rate = ((double)(thisMonth - lastMonth) / lastMonth) * 100;
                this.changeRate = String.format("%+.1f%%", rate);
            } else {
                this.changeRate = "N/A";
            }
        }

        // Getters & Setters
        public Long getThisMonth() { return thisMonth; }
        public void setThisMonth(Long thisMonth) { this.thisMonth = thisMonth; }
        public Long getLastMonth() { return lastMonth; }
        public void setLastMonth(Long lastMonth) { this.lastMonth = lastMonth; }
        public String getChangeRate() { return changeRate; }
        public void setChangeRate(String changeRate) { this.changeRate = changeRate; }
    }

    // ==================== Constructors ====================
    public PublisherReviewStatsResponse() {}

    // ==================== Getters & Setters ====================

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Long getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }

    public Long getThisMonthReviews() { return thisMonthReviews; }
    public void setThisMonthReviews(Long thisMonthReviews) { this.thisMonthReviews = thisMonthReviews; }

    public Long getUnansweredReviews() { return unansweredReviews; }
    public void setUnansweredReviews(Long unansweredReviews) { this.unansweredReviews = unansweredReviews; }

    public Long getBestReviewCount() { return bestReviewCount; }
    public void setBestReviewCount(Long bestReviewCount) { this.bestReviewCount = bestReviewCount; }

    public Map<Integer, Long> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(Map<Integer, Long> ratingDistribution) { this.ratingDistribution = ratingDistribution; }

    public TrendInfo getRecentTrend() { return recentTrend; }
    public void setRecentTrend(TrendInfo recentTrend) { this.recentTrend = recentTrend; }
}