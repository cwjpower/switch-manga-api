package com.switchmanga.api.controller;

import com.switchmanga.api.dto.publisher.*;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.security.AuthUser;
import com.switchmanga.api.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.switchmanga.api.dto.series.*;
import com.switchmanga.api.service.SeriesService;
import java.util.List;
import com.switchmanga.api.dto.volume.*;
import com.switchmanga.api.service.VolumeService;
import jakarta.validation.Valid;

/**
 * Publisher Portal Controller
 * 출판사가 자신의 정보만 관리하는 API
 */
@Tag(name = "Publisher Portal", description = "출판사 포털 API (PUBLISHER 전용)")
@RestController
@RequestMapping("/api/v1/publishers/me")
@RequiredArgsConstructor
public class PublisherPortalController {
    
    private final PublisherService publisherService;
    private final SeriesService seriesService;  // ← 추가!
    private final VolumeService volumeService;


    @Operation(summary = "내 출판사 정보 조회", description = "로그인한 출판사의 정보를 조회합니다")
    @GetMapping
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<PublisherInfoResponse> getMyPublisher(@AuthUser User user) {
        PublisherInfoResponse publisher = publisherService.getMyPublisher(user);
        return ResponseEntity.ok(publisher);
    }
    
    @Operation(summary = "내 출판사 정보 수정", description = "로그인한 출판사의 정보를 수정합니다")
    @PutMapping
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<PublisherInfoResponse> updateMyPublisher(
            @AuthUser User user,
            @Valid @RequestBody PublisherUpdateRequest request) {
        PublisherInfoResponse updated = publisherService.updateMyPublisher(user, request);
        return ResponseEntity.ok(updated);
    }
    
    @Operation(summary = "내 출판사 통계 조회", description = "로그인한 출판사의 통계 정보를 조회합니다")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<PublisherStatsResponse> getMyStats(@AuthUser User user) {
        PublisherStatsResponse stats = publisherService.getMyStats(user);
        return ResponseEntity.ok(stats);
    }

    // ========================================
    // 🔒 Series 관리 API
    // ========================================

     @Operation(summary = "내 시리즈 목록 조회", description = "로그인한 출판사의 시리즈 목록을 조회합니다")
    @GetMapping("/series")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<List<SeriesListResponse>> getMySeries(@AuthUser User user) {
        List<SeriesListResponse> series = seriesService.getMySeries(user);
        return ResponseEntity.ok(series);
    }

    @Operation(summary = "시리즈 생성", description = "새로운 시리즈를 생성합니다")
    @PostMapping("/series")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<SeriesDetailResponse> createSeries(
            @AuthUser User user,
            @Valid @RequestBody SeriesCreateRequest request) {
        SeriesDetailResponse created = seriesService.createMySeries(user, request);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "시리즈 상세 조회", description = "특정 시리즈의 상세 정보를 조회합니다")
    @GetMapping("/series/{seriesId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<SeriesDetailResponse> getSeriesDetail(
            @AuthUser User user,
            @PathVariable Long seriesId) {
        SeriesDetailResponse detail = seriesService.getMySeriesDetail(user, seriesId);
        return ResponseEntity.ok(detail);
    }

    @Operation(summary = "시리즈 수정", description = "시리즈 정보를 수정합니다")
    @PutMapping("/series/{seriesId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<SeriesDetailResponse> updateSeries(
            @AuthUser User user,
            @PathVariable Long seriesId,
            @Valid @RequestBody SeriesUpdateRequest request) {
        SeriesDetailResponse updated = seriesService.updateMySeries(user, seriesId, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "시리즈 삭제", description = "시리즈를 삭제합니다 (Soft Delete)")
    @DeleteMapping("/series/{seriesId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Void> deleteSeries(
            @AuthUser User user,
            @PathVariable Long seriesId) {
        seriesService.deleteMySeries(user, seriesId);
        return ResponseEntity.noContent().build();
    }
// ========================================
    // 🔒 Volume 관리 API
    // ========================================

    @Operation(summary = "내 Volume 목록 조회", description = "로그인한 출판사의 Volume 목록을 조회합니다")
    @GetMapping("/volumes")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<List<VolumeListResponse>> getMyVolumes(@AuthUser User user) {
        List<VolumeListResponse> volumes = volumeService.getMyVolumes(user);
        return ResponseEntity.ok(volumes);
    }

    @Operation(summary = "Volume 생성", description = "새로운 Volume을 생성합니다")
    @PostMapping("/volumes")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<VolumeDetailResponse> createVolume(
            @AuthUser User user,
            @Valid @RequestBody VolumeCreateRequest request) {
        VolumeDetailResponse created = volumeService.createMyVolume(user, request);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Volume 상세 조회", description = "특정 Volume의 상세 정보를 조회합니다")
    @GetMapping("/volumes/{volumeId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<VolumeDetailResponse> getVolumeDetail(
            @AuthUser User user,
            @PathVariable Long volumeId) {
        VolumeDetailResponse detail = volumeService.getMyVolumeDetail(user, volumeId);
        return ResponseEntity.ok(detail);
    }

    @Operation(summary = "Volume 수정", description = "Volume 정보를 수정합니다")
    @PutMapping("/volumes/{volumeId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<VolumeDetailResponse> updateVolume(
            @AuthUser User user,
            @PathVariable Long volumeId,
            @Valid @RequestBody VolumeUpdateRequest request) {
        VolumeDetailResponse updated = volumeService.updateMyVolume(user, volumeId, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Volume 삭제", description = "Volume을 삭제합니다 (Soft Delete)")
    @DeleteMapping("/volumes/{volumeId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Void> deleteVolume(
            @AuthUser User user,
            @PathVariable Long volumeId) {
        volumeService.deleteMyVolume(user, volumeId);
        return ResponseEntity.noContent().build();
    }



}
