package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository
 * 주문 관련 데이터베이스 접근
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 주문번호로 주문 조회
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * 사용자별 주문 목록 조회
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 출판사별 총 주문 수 조회
     * Order → OrderItem → Volume → Series → Publisher 관계 추적
     */
    @Query("SELECT COUNT(DISTINCT o.id) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.volume v " +
           "JOIN v.series s " +
           "WHERE s.publisher.id = :publisherId")
    Long countByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 출판사별 총 매출 계산
     * Order의 totalAmount 합계 (출판사의 모든 Volume이 포함된 주문)
     */
    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0.0) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.volume v " +
           "JOIN v.series s " +
           "WHERE s.publisher.id = :publisherId " +
           "AND o.status = 'COMPLETED'")
    Double calculateTotalRevenueByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 출판사별 이번 달 매출 계산
     */
    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0.0) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.volume v " +
           "JOIN v.series s " +
           "WHERE s.publisher.id = :publisherId " +
           "AND o.status = 'COMPLETED' " +
           "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
           "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    Double calculateMonthlyRevenueByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 출판사별 특정 기간 매출 계산
     */
    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0.0) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.volume v " +
           "JOIN v.series s " +
           "WHERE s.publisher.id = :publisherId " +
           "AND o.status = 'COMPLETED' " +
           "AND o.createdAt BETWEEN :startDate AND :endDate")
    Double calculateRevenueByPublisherIdAndDateRange(
            @Param("publisherId") Long publisherId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 시리즈별 총 주문 수
     */
    @Query("SELECT COUNT(DISTINCT o.id) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.volume v " +
           "WHERE v.series.id = :seriesId")
    Long countBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * 시리즈별 매출 계산
     */
    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0.0) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.volume v " +
           "WHERE v.series.id = :seriesId " +
           "AND o.status = 'COMPLETED'")
    Double calculateRevenueBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * Volume별 주문 수
     */
    @Query("SELECT COUNT(DISTINCT o.id) FROM Order o " +
           "JOIN o.orderItems oi " +
           "WHERE oi.volume.id = :volumeId")
    Long countByVolumeId(@Param("volumeId") Long volumeId);

    /**
     * 주문 상태별 조회
     */
    List<Order> findByStatus(String status);

    /**
     * 사용자별 + 상태별 주문 조회
     */
    List<Order> findByUserIdAndStatus(Long userId, String status);

    /**
     * 특정 기간의 주문 조회
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 오늘의 주문 조회
     */
    @Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE ORDER BY o.createdAt DESC")
    List<Order> findTodayOrders();

    /**
     * 최신 주문 Top N (생성일 기준)
     */
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findTopOrdersByCreatedAtDesc();

    /**
     * 최신 주문 Top 10 (생성일 기준)
     * Spring Data JPA Query Method
     */
    List<Order> findTop10ByOrderByCreatedAtDesc();

    /**
     * 사용자별 총 지출액 계산
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.user.id = :userId AND o.status = 'COMPLETED'")
    Double getTotalSpentByUserId(@Param("userId") Long userId);

    /**
     * 상태별 주문 수 조회
     */
    Long countByStatus(String status);
}
