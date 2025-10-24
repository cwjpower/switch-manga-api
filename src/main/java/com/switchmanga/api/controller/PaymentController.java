package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Payment;
import com.switchmanga.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 1. 전체 결제 목록 조회 (관리자용)
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // 2. 결제 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    // 3. 결제번호로 조회
    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<Payment> getPaymentByPaymentNumber(@PathVariable String paymentNumber) {
        Payment payment = paymentService.getPaymentByPaymentNumber(paymentNumber);
        return ResponseEntity.ok(payment);
    }

    // 4. 주문별 결제 조회
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    // 5. 상태별 결제 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    // 6. 최근 결제 조회
    @GetMapping("/recent")
    public ResponseEntity<List<Payment>> getRecentPayments() {
        List<Payment> payments = paymentService.getRecentPayments();
        return ResponseEntity.ok(payments);
    }

    // 7. 결제 생성
    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestBody Map<String, Object> requestBody) {
        
        try {
            Long orderId = Long.valueOf(requestBody.get("orderId").toString());
            
            Payment payment = new Payment();
            if (requestBody.containsKey("paymentMethod")) {
                payment.setPaymentMethod(requestBody.get("paymentMethod").toString());
            }
            if (requestBody.containsKey("pgProvider")) {
                payment.setPgProvider(requestBody.get("pgProvider").toString());
            }
            if (requestBody.containsKey("cardNumber")) {
                payment.setCardNumber(requestBody.get("cardNumber").toString());
            }
            if (requestBody.containsKey("cardCompany")) {
                payment.setCardCompany(requestBody.get("cardCompany").toString());
            }
            if (requestBody.containsKey("installmentMonths")) {
                payment.setInstallmentMonths(Integer.valueOf(requestBody.get("installmentMonths").toString()));
            }
            
            Payment created = paymentService.createPayment(payment, orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            System.out.println("Error creating payment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 8. 결제 완료 처리
    @PostMapping("/{id}/complete")
    public ResponseEntity<Payment> completePayment(
            @PathVariable Long id,
            @RequestParam String pgTransactionId) {
        
        try {
            Payment payment = paymentService.completePayment(id, pgTransactionId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 9. 결제 실패 처리
    @PostMapping("/{id}/fail")
    public ResponseEntity<Payment> failPayment(
            @PathVariable Long id,
            @RequestParam String failureReason) {
        
        try {
            Payment payment = paymentService.failPayment(id, failureReason);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 10. 환불 처리
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(
            @PathVariable Long id,
            @RequestParam String refundReason) {
        
        try {
            Payment payment = paymentService.refundPayment(id, refundReason);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 11. 결제 통계 (상태별)
    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Long>> getPaymentStatsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("PENDING", paymentService.getPaymentCountByStatus("PENDING"));
        stats.put("COMPLETED", paymentService.getPaymentCountByStatus("COMPLETED"));
        stats.put("FAILED", paymentService.getPaymentCountByStatus("FAILED"));
        stats.put("REFUNDED", paymentService.getPaymentCountByStatus("REFUNDED"));
        
        return ResponseEntity.ok(stats);
    }

    // 12. 오늘 결제 총액
    @GetMapping("/stats/today")
    public ResponseEntity<Map<String, Object>> getTodayStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayTotalAmount", paymentService.getTodayTotalAmount());
        
        return ResponseEntity.ok(stats);
    }

    // 13. 총 환불 금액
    @GetMapping("/stats/refund")
    public ResponseEntity<Map<String, Object>> getRefundStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRefundAmount", paymentService.getTotalRefundAmount());
        
        return ResponseEntity.ok(stats);
    }
}
