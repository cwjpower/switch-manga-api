package com.switchmanga.api.dto.order;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderStatsResponse {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal thisMonthRevenue;
    private BigDecimal todayRevenue;
}