package com.switchmanga.api.controller;

import com.switchmanga.api.dto.publisher.*;
import com.switchmanga.api.dto.series.*;
import com.switchmanga.api.dto.volume.*;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> result = publisherService.getMyVolumes(user, page, size, seriesId);

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
}