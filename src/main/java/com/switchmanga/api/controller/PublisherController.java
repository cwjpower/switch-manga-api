package com.switchmanga.api.controller;

import com.switchmanga.api.dto.publisher.*;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.security.AuthUser;
import com.switchmanga.api.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Publisher Controller (ADMIN 전용)
 * 슈퍼 관리자가 모든 출판사를 관리하는 API
 */
@Tag(name = "Publisher (Admin)", description = "출판사 관리 API (ADMIN 전용)")
@RestController
@RequestMapping("/api/v1/publishers")
@RequiredArgsConstructor
public class PublisherController {
    
    private final PublisherService publisherService;
    
    @Operation(summary = "모든 출판사 조회", description = "전체 출판사 목록을 조회합니다 (ADMIN 전용)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PublisherInfoResponse>> getAllPublishers(@AuthUser User admin) {
        List<PublisherInfoResponse> publishers = publisherService.getAllPublishers(admin);
        return ResponseEntity.ok(publishers);
    }
    
    @Operation(summary = "출판사 상세 조회", description = "특정 출판사의 상세 정보를 조회합니다 (ADMIN 전용)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherInfoResponse> getPublisher(
            @AuthUser User admin,
            @PathVariable Long id) {
        PublisherInfoResponse publisher = publisherService.getPublisherById(admin, id);
        return ResponseEntity.ok(publisher);
    }
    
    @Operation(summary = "출판사 생성", description = "새로운 출판사를 등록합니다 (ADMIN 전용)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherInfoResponse> createPublisher(
            @AuthUser User admin,
            @Valid @RequestBody PublisherCreateRequest request) {
        PublisherInfoResponse created = publisherService.createPublisher(admin, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @Operation(summary = "출판사 정보 수정", description = "출판사 정보를 수정합니다 (ADMIN 전용)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherInfoResponse> updatePublisher(
            @AuthUser User admin,
            @PathVariable Long id,
            @Valid @RequestBody PublisherUpdateRequest request) {
        PublisherInfoResponse updated = publisherService.updatePublisherByAdmin(admin, id, request);
        return ResponseEntity.ok(updated);
    }
    
    @Operation(summary = "출판사 삭제", description = "출판사를 비활성화합니다 (Soft Delete, ADMIN 전용)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePublisher(
            @AuthUser User admin,
            @PathVariable Long id) {
        publisherService.deletePublisher(admin, id);
        return ResponseEntity.noContent().build();
    }
}
