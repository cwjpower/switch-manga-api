package com.switchmanga.api.repository;

import com.switchmanga.api.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 주문별 항목 조회
    List<OrderItem> findByOrderId(Long orderId);

    // 특정 Volume의 판매 개수
    Long countByVolumeId(Long volumeId);

    // 주문별 항목 개수
    Long countByOrderId(Long orderId);
}
