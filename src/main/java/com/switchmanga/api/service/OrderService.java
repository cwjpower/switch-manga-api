package com.switchmanga.api.service;

import com.switchmanga.api.entity.Order;
import com.switchmanga.api.entity.OrderItem;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.repository.OrderRepository;
import com.switchmanga.api.repository.OrderItemRepository;
import com.switchmanga.api.repository.UserRepository;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final VolumeRepository volumeRepository;

    // 전체 주문 목록 조회
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 주문 상세 조회
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. ID: " + id));
    }

    // 주문번호로 조회
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. 주문번호: " + orderNumber));
    }

    // 사용자별 주문 목록 조회
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 상태별 주문 조회
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    // 오늘 주문 조회
    public List<Order> getTodayOrders() {
        return orderRepository.findTodayOrders();
    }

    // 최근 주문 조회
    public List<Order> getRecentOrders() {
        return orderRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // 주문 생성
    @Transactional
    public Order createOrder(Order order, Long userId, List<Long> volumeIds) {
        // User 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 주문번호 생성
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setStatus("PENDING");

        // Volume들을 OrderItem으로 추가
        for (Long volumeId : volumeIds) {
            Volume volume = volumeRepository.findById(volumeId)
                    .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ID: " + volumeId));

            OrderItem orderItem = new OrderItem();
            orderItem.setVolume(volume);
            orderItem.setPrice(volume.getPrice());
            orderItem.setQuantity(1);
            orderItem.calculateSubtotal();

            order.addOrderItem(orderItem);
        }

        // 총 금액 계산
        order.calculateTotalAmount();

        return orderRepository.save(order);
    }

    // 주문 상태 변경
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);

        // 상태 유효성 검사
        if (!isValidStatus(status)) {
            throw new RuntimeException("유효하지 않은 주문 상태입니다: " + status);
        }

        order.setStatus(status);

        // 결제 완료 시간 기록
        if ("PAID".equals(status) && order.getPaidAt() == null) {
            order.setPaidAt(LocalDateTime.now());
        }

        // 취소 시간 기록
        if ("CANCELLED".equals(status) && order.getCancelledAt() == null) {
            order.setCancelledAt(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long id, Long userId) {
        Order order = getOrderById(id);

        // 본인 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 주문만 취소할 수 있습니다.");
        }

        // 이미 결제된 주문은 취소 불가 (환불로 처리해야 함)
        if ("PAID".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("이미 결제가 완료된 주문은 취소할 수 없습니다. 환불을 요청해주세요.");
        }

        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    // 주문 삭제 (관리자용)
    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    // 주문 항목 조회
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    // 주문번호 생성
    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = orderRepository.count() + 1;
        return String.format("ORD-%s-%04d", date, count);
    }

    // 상태 유효성 검사
    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || 
               status.equals("PAID") || 
               status.equals("COMPLETED") || 
               status.equals("CANCELLED") || 
               status.equals("REFUNDED");
    }

    // 사용자별 총 구매 금액
    public Double getTotalSpentByUser(Long userId) {
        Double total = orderRepository.getTotalSpentByUserId(userId);
        return total != null ? total : 0.0;
    }

    // 주문 통계
    public Long getOrderCountByStatus(String status) {
        return orderRepository.countByStatus(status);
    }
}
