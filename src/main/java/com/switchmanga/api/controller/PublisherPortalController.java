package com.switchmanga.api.controller;

import com.switchmanga.api.dto.publisher.*;
import com.switchmanga.api.dto.series.*;
import com.switchmanga.api.dto.volume.*;
import com.switchmanga.api.dto.order.*;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.switchmanga.api.dto.response.RevenueStatsResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/publishers")
@RequiredArgsConstructor
@Slf4j
public class PublisherPortalController {

    private final PublisherService publisherService;

    // ========================================
    // Series 관련 API
    // ========================================

    /**
     * 내 시리즈 목록 조회
     */
    @GetMapping("/me/series")
    public ResponseEntity<?> getMySeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> result = publisherService.getMySeries(user, page, size, status, search);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("msg", "조회 성공");
            response.putAll(result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("시리즈 목록 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 시리즈 상세 조회
     */
    @GetMapping("/me/series/{id}")
    public ResponseEntity<?> getMySeriesDetail(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            SeriesDetailResponse result = publisherService.getMySeriesDetail(user, id);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "조회 성공",
                    "data", result
            ));
        } catch (Exception e) {
            log.error("시리즈 상세 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 시리즈 생성
     */
    @PostMapping("/me/series")
    public ResponseEntity<?> createSeries(
            @Valid @RequestBody SeriesCreateRequest request,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Series series = publisherService.createSeries(user, request);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "시리즈가 생성되었습니다",
                    "data", SeriesDetailResponse.from(series)
            ));
        } catch (Exception e) {
            log.error("시리즈 생성 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 시리즈 수정
     */
    @PutMapping("/me/series/{id}")
    public ResponseEntity<?> updateSeries(
            @PathVariable Long id,
            @Valid @RequestBody SeriesUpdateRequest request,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Series series = publisherService.updateSeries(user, id, request);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "시리즈가 수정되었습니다",
                    "data", SeriesDetailResponse.from(series)
            ));
        } catch (Exception e) {
            log.error("시리즈 수정 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 시리즈 삭제
     */
    @DeleteMapping("/me/series/{id}")
    public ResponseEntity<?> deleteSeries(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            publisherService.deleteSeries(user, id);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "시리즈가 삭제되었습니다"
            ));
        } catch (Exception e) {
            log.error("시리즈 삭제 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    // ========================================
    // Volume 관련 API
    // ========================================

    /**
     * 내 볼륨 목록 조회
     */
    @GetMapping("/me/volumes")
    public ResponseEntity<?> getMyVolumes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long seriesId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "desc") String sort,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> result = publisherService.getMyVolumes(
                    user, page, size, seriesId, search, status, sort
            );

            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("msg", "조회 성공");
            response.putAll(result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("볼륨 목록 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 볼륨 상세 조회
     */
    @GetMapping("/me/volumes/{id}")
    public ResponseEntity<?> getMyVolumeDetail(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            VolumeDetailResponse result = publisherService.getMyVolumeDetail(user, id);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "조회 성공",
                    "data", result
            ));
        } catch (Exception e) {
            log.error("볼륨 상세 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 볼륨 생성
     */
    @PostMapping("/me/volumes")
    public ResponseEntity<?> createVolume(
            @Valid @RequestBody VolumeCreateRequest request,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Volume volume = publisherService.createVolume(user, request);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "볼륨이 생성되었습니다",
                    "data", VolumeDetailResponse.from(volume)
            ));
        } catch (Exception e) {
            log.error("볼륨 생성 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 볼륨 수정
     */
    @PutMapping("/me/volumes/{id}")
    public ResponseEntity<?> updateVolume(
            @PathVariable Long id,
            @Valid @RequestBody VolumeUpdateRequest request,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Volume volume = publisherService.updateVolume(user, id, request);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "볼륨이 수정되었습니다",
                    "data", VolumeDetailResponse.from(volume)
            ));
        } catch (Exception e) {
            log.error("볼륨 수정 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 볼륨 삭제
     */
    @DeleteMapping("/me/volumes/{id}")
    public ResponseEntity<?> deleteVolume(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            publisherService.deleteVolume(user, id);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "볼륨이 삭제되었습니다"
            ));
        } catch (Exception e) {
            log.error("볼륨 삭제 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    // ========================================
    // Publisher 정보 API
    // ========================================

    /**
     * 내 출판사 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PublisherInfoResponse result = publisherService.getMyInfo(user);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "조회 성공",
                    "data", result
            ));
        } catch (Exception e) {
            log.error("출판사 정보 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 내 출판사 정보 수정
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyInfo(
            @Valid @RequestBody PublisherUpdateRequest request,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            PublisherInfoResponse result = publisherService.updateMyInfo(user, request);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "출판사 정보가 수정되었습니다",
                    "data", result
            ));
        } catch (Exception e) {
            log.error("출판사 정보 수정 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 내 출판사 통계 조회
     */
    @GetMapping("/me/stats")
    public ResponseEntity<?> getMyStats(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PublisherStatsResponse result = publisherService.getMyStats(user);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "조회 성공",
                    "data", result
            ));
        } catch (Exception e) {
            log.error("출판사 통계 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    // ========================================
    // Order 관련 API (내 주문 내역)
    // ========================================

    /**
     * 내 주문 목록 조회 (통합 검색 지원)
     */
    @GetMapping("/me/orders")
    public ResponseEntity<?> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(required = false) String keyword,  // ✅ 통합 검색 키워드 추가
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> result = publisherService.getMyOrders(
                    user, page, size, status, startDate, endDate, sort, keyword  // ✅ keyword 전달
            );

            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("msg", "조회 성공");
            response.putAll(result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("주문 목록 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 내 주문 통계 조회
     */
    @GetMapping("/me/orders/stats")
    public ResponseEntity<?> getMyOrderStats(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            OrderStatsResponse result = publisherService.getMyOrderStats(user);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "조회 성공",
                    "data", result
            ));
        } catch (Exception e) {
            log.error("주문 통계 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }

    /**
     * 베스트셀러 조회
     */
    @GetMapping("/me/orders/bestsellers")
    public ResponseEntity<?> getMyBestsellers(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            List<BestsellerResponse> result = publisherService.getMyBestsellers(user, limit);

            return ResponseEntity.ok(Map.of(
                    "code", 0,
                    "msg", "조회 성공",
                    "data", result
            ));
        } catch (Exception e) {
            log.error("베스트셀러 조회 실패", e);
            return ResponseEntity.status(500).body(
                    Map.of("code", 1, "msg", e.getMessage())
            );
        }
    }
    // ========================================
    // 🆕 매출 현황 API
    // ========================================

    /**
     * 내 매출 현황 조회
     * GET /api/v1/publishers/me/revenue
     *
     * @param period 기간 (today, week, month, year, custom)
     * @param startDate 시작일 (custom 기간용)
     * @param endDate 종료일 (custom 기간용)
     */
    @GetMapping("/me/revenue")
    public ResponseEntity<?> getMyRevenue(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            RevenueStatsResponse response = publisherService.getMyRevenue(user, period, startDate, endDate);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("msg", "조회 성공");
            result.put("data", response);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 1);
            error.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 1);
            error.put("msg", "매출 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}