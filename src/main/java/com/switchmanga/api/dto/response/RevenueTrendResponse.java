package com.switchmanga.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTrendResponse {

    private String period;  // today, week, month, year
    private List<TrendPoint> current;
    private List<TrendPoint> previous;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private String label;      // 날짜/시간 라벨 (예: "12/01", "Mon", "14:00")
        private BigDecimal revenue;
        private Long sales;
    }
}