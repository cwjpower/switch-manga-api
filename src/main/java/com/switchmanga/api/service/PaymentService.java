package com.switchmanga.api.service;

import com.switchmanga.api.entity.Order;
import com.switchmanga.api.entity.Payment;
import com.switchmanga.api.repository.OrderRepository;
import com.switchmanga.api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // 전체 결제 목록 조회
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // 결제 상세 조회
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다. ID: " + id));
    }

    // 결제번호로 조회
    public Payment getPaymentByPaymentNumber(String paymentNumber) {
        return paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다. 결제번호: " + paymentNumber));
    }

    // 주문별 결제 조회
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문의 결제 정보를 찾을 수 없습니다. 주문 ID: " + orderId));
    }

    // 상태별 결제 조회
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    // 최근 결제 조회
    public List<Payment> getRecentPayments() {
        return paymentRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // 결제 생성
    @Transactional
    public Payment createPayment(Payment payment, Long orderId) {
        // Order 존재 확인
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. ID: " + orderId));

        // 이미 결제가 있는지 확인
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new RuntimeException("이미 결제가 진행된 주문입니다.");
        }

        // 주문 상태 확인
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("결제 대기 중인 주문만 결제할 수 있습니다.");
        }

        // 결제번호 생성
        String paymentNumber = generatePaymentNumber();
        payment.setPaymentNumber(paymentNumber);
        payment.setOrder(order);
        payment.setAmount(order.getFinalAmount());
        payment.setStatus("PENDING");

        return paymentRepository.save(payment);
    }

    // 결제 완료 처리
    @Transactional
    public Payment completePayment(Long id, String pgTransactionId) {
        Payment payment = getPaymentById(id);

        // 이미 완료된 결제인지 확인
        if ("COMPLETED".equals(payment.getStatus())) {
            throw new RuntimeException("이미 완료된 결제입니다.");
        }

        // 결제 완료 처리
        payment.setStatus("COMPLETED");
        payment.setPgTransactionId(pgTransactionId);
        payment.setCompletedAt(LocalDateTime.now());

        // 주문 상태도 업데이트
        Order order = payment.getOrder();
        order.setStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    // 결제 실패 처리
    @Transactional
    public Payment failPayment(Long id, String failureReason) {
        Payment payment = getPaymentById(id);

        payment.setStatus("FAILED");
        payment.setFailureReason(failureReason);
        payment.setFailedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    // 환불 처리
    @Transactional
    public Payment refundPayment(Long id, String refundReason) {
        Payment payment = getPaymentById(id);

        // 완료된 결제만 환불 가능
        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new RuntimeException("완료된 결제만 환불할 수 있습니다.");
        }

        // 환불 처리
        payment.setStatus("REFUNDED");
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundReason(refundReason);
        payment.setRefundedAt(LocalDateTime.now());

        // 주문 상태도 업데이트
        Order order = payment.getOrder();
        order.setStatus("REFUNDED");
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    // 결제번호 생성
    private String generatePaymentNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = paymentRepository.count() + 1;
        return String.format("PAY-%s-%04d", date, count);
    }

    // 결제 통계
    public Long getPaymentCountByStatus(String status) {
        return paymentRepository.countByStatus(status);
    }

    // 오늘 결제 총액
    public Double getTodayTotalAmount() {
        Double total = paymentRepository.getTodayTotalAmount();
        return total != null ? total : 0.0;
    }

    // 총 환불 금액
    public Double getTotalRefundAmount() {
        Double total = paymentRepository.getTotalRefundAmount();
        return total != null ? total : 0.0;
    }
}
