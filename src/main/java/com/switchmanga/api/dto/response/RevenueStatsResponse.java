package com.switchmanga.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * 매출 통계 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsResponse {

    // 기간 정보
    private PeriodInfo period;

    // 요약 통계
    private SummaryStats summary;

    // 수익 배분
    private RevenueDistribution distribution;

    // 시리즈별 매출 Top 10
    private List<SeriesRevenue> topSeries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodInfo {
        private String type;        // today, week, month, year, custom
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryStats {
        private BigDecimal totalRevenue;      // 총 매출
        private BigDecimal netRevenue;        // 순수익 (70%)
        private Long totalSales;              // 판매량 (건수)
        private BigDecimal averagePrice;      // 평균 단가

        // 전 기간 대비 변화율
        private String revenueChangeRate;     // ex: "+14.3%"
        private String salesChangeRate;       // ex: "+12.5%"

        // 전 기간 데이터 (비교용)
        private BigDecimal previousRevenue;
        private Long previousSales;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDistribution {
        private BigDecimal totalRevenue;      // 총 매출
        private BigDecimal publisherShare;    // 출판사 몫 (70%)
        private BigDecimal platformFee;       // 플랫폼 수수료 (30%)
        private Integer shareRatio;           // 배분 비율 (70)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesRevenue {
        private Integer rank;
        private Long seriesId;
        private String seriesTitle;
        private String coverImage;
        private Long salesCount;
        private BigDecimal revenue;
        private Double percentage;  // 전체 매출 중 비율
    }

    /**
     * 변화율 계산 유틸리티
     */
    public static String calculateChangeRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            if (current != null && current.compareTo(BigDecimal.ZERO) > 0) {
                return "+100%";
            }
            return "0%";
        }

        BigDecimal change = current.subtract(previous);
        BigDecimal rate = change.multiply(BigDecimal.valueOf(100))
                .divide(previous, 1, RoundingMode.HALF_UP);

        String sign = rate.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return sign + rate.setScale(1, RoundingMode.HALF_UP) + "%";
    }

    public static String calculateChangeRate(Long current, Long previous) {
        return calculateChangeRate(
                BigDecimal.valueOf(current != null ? current : 0),
                BigDecimal.valueOf(previous != null ? previous : 0)
        );
    }
}