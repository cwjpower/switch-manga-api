package com.switchmanga.api.controller;

import com.switchmanga.api.dto.review.*;  // ← 수정됨!
import com.switchmanga.api.entity.ReviewReply;
import com.switchmanga.api.entity.ReviewReport;
import com.switchmanga.api.service.PublisherReviewService;
import com.switchmanga.api.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 출판사용 리뷰 관리 API
 *
 * 기본 경로: /api/v1/publishers/me/reviews
 */
@RestController
@RequestMapping("/api/v1/publishers/me/reviews")
public class PublisherReviewController {

    @Autowired
    private PublisherReviewService publisherReviewService;

    // ==================== 리뷰 목록/통계 ====================

    /**
     * 리뷰 목록 조회
     * GET /api/v1/publishers/me/reviews
     */
    @GetMapping
    public ResponseEntity<?> getReviews(
            HttpServletRequest request,
            @RequestParam(required = false) Long seriesId,
            @RequestParam(required = false) Long volumeId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Long publisherId = getPublisherIdFromRequest(request);

            // 정렬 설정
            Sort sortOrder = getSortOrder(sort);
            Pageable pageable = PageRequest.of(page, size, sortOrder);

            Page<PublisherReviewResponse> reviews = publisherReviewService.getPublisherReviews(
                    publisherId, seriesId, volumeId, rating, status, keyword, pageable);

            return ResponseEntity.ok(ApiResponse.success("조회 성공", reviews));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 리뷰 통계 조회
     * GET /api/v1/publishers/me/reviews/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getReviewStats(HttpServletRequest request) {

        try {
            Long publisherId = getPublisherIdFromRequest(request);

            PublisherReviewStatsResponse stats = publisherReviewService.getPublisherReviewStats(publisherId);

            return ResponseEntity.ok(ApiResponse.success("조회 성공", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("조회 실패: " + e.getMessage()));
        }
    }

    // ==================== 답글 CRUD ====================

    /**
     * 답글 작성
     * POST /api/v1/publishers/me/reviews/{reviewId}/reply
     */
    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<?> createReply(
            HttpServletRequest request,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewReplyRequest replyRequest) {

        try {
            Long publisherId = getPublisherIdFromRequest(request);

            ReviewReply reply = publisherReviewService.createReply(
                    publisherId, reviewId, replyRequest.getContent());

            Map<String, Object> data = new HashMap<>();
            data.put("id", reply.getId());
            data.put("reviewId", reviewId);
            data.put("content", reply.getContent());
            data.put("createdAt", reply.getCreatedAt());

            return ResponseEntity.ok(ApiResponse.success("답글이 등록되었습니다", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("답글 등록 실패: " + e.getMessage()));
        }
    }

    /**
     * 답글 수정
     * PUT /api/v1/publishers/me/reviews/{reviewId}/reply
     */
    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<?> updateReply(
            HttpServletRequest request,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewReplyRequest replyRequest) {

        try {
            Long publisherId = getPublisherIdFromRequest(request);

            ReviewReply reply = publisherReviewService.updateReply(
                    publisherId, reviewId, replyRequest.getContent());

            Map<String, Object> data = new HashMap<>();
            data.put("id", reply.getId());
            data.put("reviewId", reviewId);
            data.put("content", reply.getContent());
            data.put("updatedAt", reply.getUpdatedAt());

            return ResponseEntity.ok(ApiResponse.success("답글이 수정되었습니다", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("답글 수정 실패: " + e.getMessage()));
        }
    }

    /**
     * 답글 삭제
     * DELETE /api/v1/publishers/me/reviews/{reviewId}/reply
     */
    @DeleteMapping("/{reviewId}/reply")
    public ResponseEntity<?> deleteReply(
            HttpServletRequest request,
            @PathVariable Long reviewId) {

        try {
            Long publisherId = getPublisherIdFromRequest(request);

            publisherReviewService.deleteReply(publisherId, reviewId);

            return ResponseEntity.ok(ApiResponse.success("답글이 삭제되었습니다", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("답글 삭제 실패: " + e.getMessage()));
        }
    }

    // ==================== 신고 ====================

    /**
     * 리뷰 신고
     * POST /api/v1/publishers/me/reviews/{reviewId}/report
     */
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<?> reportReview(
            HttpServletRequest request,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewReportRequest reportRequest) {

        try {
            Long publisherId = getPublisherIdFromRequest(request);

            ReviewReport report = publisherReviewService.reportReview(
                    publisherId, reviewId,
                    reportRequest.getReason(),
                    reportRequest.getDescription());

            Map<String, Object> data = new HashMap<>();
            data.put("id", report.getId());
            data.put("reviewId", reviewId);
            data.put("reason", report.getReason().name());
            data.put("createdAt", report.getCreatedAt());

            return ResponseEntity.ok(ApiResponse.success("신고가 접수되었습니다", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("신고 실패: " + e.getMessage()));
        }
    }

    // ==================== Helper Methods ====================

    /**
     * 요청에서 출판사 ID 추출
     * (JWT 토큰 또는 세션에서 추출)
     */
    private Long getPublisherIdFromRequest(HttpServletRequest request) {
        // 현재는 임시로 request attribute에서 가져옴
        // 실제로는 JWT 토큰에서 추출해야 함
        Object publisherIdObj = request.getAttribute("publisherId");
        if (publisherIdObj == null) {
            // 테스트용 기본값 (나중에 제거)
            return 1L;
        }
        return (Long) publisherIdObj;
    }

    /**
     * 정렬 순서 설정
     */
    private Sort getSortOrder(String sort) {
        return switch (sort.toLowerCase()) {
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "rating_high" -> Sort.by(Sort.Direction.DESC, "rating");
            case "rating_low" -> Sort.by(Sort.Direction.ASC, "rating");
            case "likes" -> Sort.by(Sort.Direction.DESC, "likeCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // latest
        };
    }
}