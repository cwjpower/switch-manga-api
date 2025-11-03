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
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "orderItems"})
    private Order order;

    @Column(name = "payment_number", unique = true, nullable = false, length = 50)
    private String paymentNumber;  // 결제번호 (예: PAY-20251024-0001)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;  // 결제 금액

    @Column(name = "payment_method", length = 20, nullable = false)
    private String paymentMethod;  // 결제 수단: CARD, CASH, POINT

    @Column(length = 20, nullable = false)
    private String status = "PENDING";  // 결제 상태: PENDING, COMPLETED, FAILED, REFUNDED

    @Column(name = "pg_provider", length = 50)
    private String pgProvider;  // PG사: 토스, 카카오페이, 네이버페이 등

    @Column(name = "pg_transaction_id", length = 100)
    private String pgTransactionId;  // PG사 거래 ID

    @Column(name = "card_number", length = 20)
    private String cardNumber;  // 카드번호 (마스킹: ****-****-****-1234)

    @Column(name = "card_company", length = 50)
    private String cardCompany;  // 카드사

    @Column(name = "installment_months")
    private Integer installmentMonths = 0;  // 할부 개월 (0: 일시불)

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;  // 환불 금액

    @Column(name = "refund_reason", length = 500)
    private String refundReason;  // 환불 사유

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;  // 결제 완료 시간

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;  // 환불 완료 시간

    @Column(name = "failed_at")
    private LocalDateTime failedAt;  // 결제 실패 시간

    @Column(name = "failure_reason", length = 500)
    private String failureReason;  // 실패 사유
}
