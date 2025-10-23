package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Review;
import com.switchmanga.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 권별 리뷰 목록 조회
    @GetMapping("/volume/{volumeId}")
    public ResponseEntity<List<Review>> getReviewsByVolume(@PathVariable Long volumeId) {
        List<Review> reviews = reviewService.getReviewsByVolume(volumeId);
        return ResponseEntity.ok(reviews);
    }

    // 2. 사용자별 리뷰 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    // 3. 리뷰 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    // 4. 권별 리뷰 개수 조회
    @GetMapping("/volume/{volumeId}/count")
    public ResponseEntity<Long> getReviewCountByVolume(@PathVariable Long volumeId) {
        Long count = reviewService.getReviewCountByVolume(volumeId);
        return ResponseEntity.ok(count);
    }

    // 5. 권별 평균 평점 조회
    @GetMapping("/volume/{volumeId}/average-rating")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable Long volumeId) {
        Double avgRating = reviewService.getAverageRatingByVolume(volumeId);
        Long count = reviewService.getReviewCountByVolume(volumeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("volumeId", volumeId);
        response.put("averageRating", avgRating);
        response.put("totalReviews", count);
        
        return ResponseEntity.ok(response);
    }

    // 6. 좋아요 많은 순으로 리뷰 조회
    @GetMapping("/volume/{volumeId}/popular")
    public ResponseEntity<List<Review>> getPopularReviews(@PathVariable Long volumeId) {
        List<Review> reviews = reviewService.getPopularReviewsByVolume(volumeId);
        return ResponseEntity.ok(reviews);
    }

    // 7. 리뷰 작성
    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestBody Review review,
            @RequestParam Long userId,
            @RequestParam Long volumeId) {
        
        try {
            Review created = reviewService.createReview(review, userId, volumeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 8. 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long id,
            @RequestBody Review review,
            @RequestParam Long userId) {
        
        try {
            Review updated = reviewService.updateReview(id, review, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 9. 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        try {
            reviewService.deleteReview(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 10. 좋아요 추가
    @PostMapping("/{id}/like")
    public ResponseEntity<Review> addLike(@PathVariable Long id) {
        Review review = reviewService.addLike(id);
        return ResponseEntity.ok(review);
    }

    // 11. 좋아요 취소
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Review> removeLike(@PathVariable Long id) {
        Review review = reviewService.removeLike(id);
        return ResponseEntity.ok(review);
    }

    // 12. 사용자가 특정 권에 리뷰 작성 여부 확인
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkUserReview(
            @RequestParam Long userId,
            @RequestParam Long volumeId) {
        
        boolean hasReviewed = reviewService.hasUserReviewedVolume(userId, volumeId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasReviewed", hasReviewed);
        
        return ResponseEntity.ok(response);
    }
}
