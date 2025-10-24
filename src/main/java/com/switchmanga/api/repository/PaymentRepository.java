package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 결제번호로 조회
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    // 주문별 결제 조회
    Optional<Payment> findByOrderId(Long orderId);

    // PG 거래 ID로 조회
    Optional<Payment> findByPgTransactionId(String pgTransactionId);

    // 상태별 결제 조회
    List<Payment> findByStatus(String status);

    // 결제 수단별 조회
    List<Payment> findByPaymentMethod(String paymentMethod);

    // 특정 기간 결제 조회
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 상태별 결제 개수
    Long countByStatus(String status);

    // 결제 수단별 총 금액
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentMethod = :paymentMethod AND p.status = 'COMPLETED'")
    Double getTotalAmountByPaymentMethod(String paymentMethod);

    // 오늘 결제 총액
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE DATE(p.completedAt) = CURRENT_DATE AND p.status = 'COMPLETED'")
    Double getTodayTotalAmount();

    // 환불 총액
    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE p.status = 'REFUNDED'")
    Double getTotalRefundAmount();

    // 최근 결제 조회
    List<Payment> findTop10ByOrderByCreatedAtDesc();
}
