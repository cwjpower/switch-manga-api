package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 주문번호로 조회
    Optional<Order> findByOrderNumber(String orderNumber);

    // 사용자별 주문 목록 조회
    List<Order> findByUserId(Long userId);

    // 사용자별 주문 목록 조회 (최신순)
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 상태별 주문 조회
    List<Order> findByStatus(String status);

    // 사용자별 + 상태별 주문 조회
    List<Order> findByUserIdAndStatus(Long userId, String status);

    // 특정 기간 주문 조회
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 사용자별 주문 개수
    Long countByUserId(Long userId);

    // 상태별 주문 개수
    Long countByStatus(String status);

    // 사용자별 총 구매 금액
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.user.id = :userId AND o.status = 'PAID'")
    Double getTotalSpentByUserId(Long userId);

    // 오늘 주문 조회
    @Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE")
    List<Order> findTodayOrders();

    // 최근 주문 조회 (limit)
    List<Order> findTop10ByOrderByCreatedAtDesc();
}
