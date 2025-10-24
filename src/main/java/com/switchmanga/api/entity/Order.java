package com.switchmanga.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;  // 주문번호 (예: ORD-20251024-0001)

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;  // 총 주문 금액

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;  // 할인 금액

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;  // 최종 결제 금액

    @Column(length = 20, nullable = false)
    private String status = "PENDING";  // 주문 상태: PENDING, PAID, COMPLETED, CANCELLED, REFUNDED

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;  // 결제 수단: CARD, CASH, POINT

    @Column(name = "coupon_code", length = 50)
    private String couponCode;  // 사용한 쿠폰 코드

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;  // 결제 완료 시간

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;  // 취소 시간

    // 주문 항목 추가 헬퍼 메서드
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    // 총 금액 계산
    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
        this.finalAmount = this.totalAmount - this.discountAmount;
    }
}
