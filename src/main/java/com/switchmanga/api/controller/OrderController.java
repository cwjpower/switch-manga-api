package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Order;
import com.switchmanga.api.entity.OrderItem;
import com.switchmanga.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. 전체 주문 목록 조회 (관리자용)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // 2. 주문 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // 3. 주문번호로 조회
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByOrderNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    // 4. 사용자별 주문 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    // 5. 상태별 주문 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // 6. 오늘 주문 조회
    @GetMapping("/today")
    public ResponseEntity<List<Order>> getTodayOrders() {
        List<Order> orders = orderService.getTodayOrders();
        return ResponseEntity.ok(orders);
    }

    // 7. 최근 주문 조회
    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        List<Order> orders = orderService.getRecentOrders();
        return ResponseEntity.ok(orders);
    }

    // 8. 주문 생성
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody Map<String, Object> requestBody) {

        try {
            Long userId = Long.valueOf(requestBody.get("userId").toString());

            // volumeIds를 안전하게 변환
            List<Long> volumeIds = new ArrayList<>();
            Object volumeIdsObj = requestBody.get("volumeIds");
            if (volumeIdsObj instanceof List) {
                for (Object id : (List<?>) volumeIdsObj) {
                    volumeIds.add(Long.valueOf(id.toString()));
                }
            }

            Order order = new Order();
            if (requestBody.containsKey("couponCode")) {
                order.setCouponCode(requestBody.get("couponCode").toString());
            }
            if (requestBody.containsKey("paymentMethod")) {
                order.setPaymentMethod(requestBody.get("paymentMethod").toString());
            }

            Order created = orderService.createOrder(order, userId, volumeIds);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            System.out.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 9. 주문 상태 변경
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            Order updated = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 10. 주문 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @RequestParam Long userId) {

        try {
            orderService.cancelOrder(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 11. 주문 항목 조회
    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        List<OrderItem> items = orderService.getOrderItems(id);
        return ResponseEntity.ok(items);
    }

    // 12. 사용자별 총 구매 금액 조회
    @GetMapping("/user/{userId}/total-spent")
    public ResponseEntity<Map<String, Object>> getTotalSpent(@PathVariable Long userId) {
        Double totalSpent = orderService.getTotalSpentByUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("totalSpent", totalSpent);

        return ResponseEntity.ok(response);
    }

    // 13. 주문 통계 (상태별)
    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Long>> getOrderStatsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("PENDING", orderService.getOrderCountByStatus("PENDING"));
        stats.put("PAID", orderService.getOrderCountByStatus("PAID"));
        stats.put("COMPLETED", orderService.getOrderCountByStatus("COMPLETED"));
        stats.put("CANCELLED", orderService.getOrderCountByStatus("CANCELLED"));
        stats.put("REFUNDED", orderService.getOrderCountByStatus("REFUNDED"));

        return ResponseEntity.ok(stats);
    }
}