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
}
