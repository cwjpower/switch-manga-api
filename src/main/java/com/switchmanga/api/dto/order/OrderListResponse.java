package com.switchmanga.api.dto.order;

import com.switchmanga.api.entity.Order;
import com.switchmanga.api.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderListResponse {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private String buyerName;
    private String buyerEmail;
    private List<OrderItemInfo> items;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;
    private String status;
    private String paymentMethod;

    @Getter
    @Builder
    public static class OrderItemInfo {
        private Long volumeId;
        private String seriesTitle;
        private String volumeTitle;
        private Integer volumeNumber;
        private String coverImage;
        private BigDecimal price;
        private Integer quantity;
    }

    public static OrderListResponse from(Order order) {
        List<OrderItemInfo> itemInfos = order.getOrderItems().stream()
                .map(item -> OrderItemInfo.builder()
                        .volumeId(item.getVolume().getId())
                        .seriesTitle(item.getVolume().getSeries().getTitle())
                        .volumeTitle(item.getVolume().getTitle())
                        .volumeNumber(item.getVolume().getVolumeNumber())
                        .coverImage(item.getVolume().getCoverImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderListResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getCreatedAt())
                .buyerName(order.getUser().getUsername())
                .buyerEmail(order.getUser().getEmail())
                .items(itemInfos)
                .totalAmount(order.getTotalAmount())
                .finalAmount(order.getFinalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }
}