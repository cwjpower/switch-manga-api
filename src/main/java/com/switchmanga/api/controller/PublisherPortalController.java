package com.switchmanga.api.controller;

import com.switchmanga.api.dto.publisher.PublisherInfoResponse;
import com.switchmanga.api.dto.publisher.PublisherStatsResponse;
import com.switchmanga.api.dto.publisher.PublisherUpdateRequest;
import com.switchmanga.api.dto.series.*;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.repository.UserRepository;
import com.switchmanga.api.service.PublisherService;
import com.switchmanga.api.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Publisher Portal Controller
 * 로그인한 Publisher가 자신의 정보를 관리하는 API
 * 
 * Base URL: /api/v1/publishers/me
 */
@RestController
@RequestMapping("/api/v1/publishers/me")
@RequiredArgsConstructor
public class PublisherPortalController {

    private final PublisherService publisherService;
    private final SeriesService seriesService;
    private final UserRepository userRepository;

    // ========================================
    // 1. Publisher 정보 관리
    // ========================================

    /**
     * 내 Publisher 정보 조회
     * GET /api/v1/publishers/me
     */
    @GetMapping
    public ResponseEntity<PublisherInfoResponse> getMyInfo() {
        User user = getCurrentUser();
        PublisherInfoResponse response = publisherService.getMyPublisher(user);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 Publisher 정보 수정
     * PUT /api/v1/publishers/me
     */
    @PutMapping
    public ResponseEntity<PublisherInfoResponse> updateMyInfo(
            @RequestBody PublisherUpdateRequest request
    ) {
        User user = getCurrentUser();
        PublisherInfoResponse response = publisherService.updateMyPublisher(user, request);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 2. 통계 조회
    // ========================================

    /**
     * 내 Publisher 통계 조회
     * GET /api/v1/publishers/me/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<PublisherStatsResponse> getMyStats() {
        User user = getCurrentUser();
        PublisherStatsResponse response = publisherService.getMyStats(user);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 3. 시리즈 관리
    // ========================================

    /**
     * 내 시리즈 목록 조회
     * GET /api/v1/publishers/me/series
     */
    @GetMapping("/series")
    public ResponseEntity<List<SeriesListResponse>> getMySeries() {
        User user = getCurrentUser();
        List<SeriesListResponse> response = seriesService.getMySeries(user);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 시리즈 생성
     * POST /api/v1/publishers/me/series
     */
    @PostMapping("/series")
    public ResponseEntity<SeriesDetailResponse> createMySeries(
            @RequestBody SeriesCreateRequest request
    ) {
        User user = getCurrentUser();
        SeriesDetailResponse response = seriesService.createMySeries(user, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 시리즈 상세 조회
     * GET /api/v1/publishers/me/series/{id}
     */
    @GetMapping("/series/{id}")
    public ResponseEntity<SeriesDetailResponse> getMySeriesDetail(@PathVariable Long id) {
        User user = getCurrentUser();
        SeriesDetailResponse response = seriesService.getMySeriesDetail(user, id);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 시리즈 수정
     * PUT /api/v1/publishers/me/series/{id}
     */
    @PutMapping("/series/{id}")
    public ResponseEntity<SeriesDetailResponse> updateMySeries(
            @PathVariable Long id,
            @RequestBody SeriesUpdateRequest request
    ) {
        User user = getCurrentUser();
        SeriesDetailResponse response = seriesService.updateMySeries(user, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 시리즈 삭제 (Soft Delete)
     * DELETE /api/v1/publishers/me/series/{id}
     */
    @DeleteMapping("/series/{id}")
    public ResponseEntity<Void> deleteMySeries(@PathVariable Long id) {
        User user = getCurrentUser();
        seriesService.deleteMySeries(user, id);
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    /**
     * 현재 로그인한 User 조회
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found: " + email));
    }
}
