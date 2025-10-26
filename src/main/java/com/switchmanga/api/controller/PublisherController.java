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
 * Publisher Controller
 * Public API + Admin API 분리
 */
@Tag(name = "Publisher", description = "출판사 API")
@RestController
@RequestMapping("/api/v1/publishers")
@RequiredArgsConstructor
public class PublisherController {
    
    private final PublisherService publisherService;
    
    // ========================================
    // 🔓 PUBLIC API (인증 불필요)
    // ========================================
    
    @Operation(summary = "[Public] 모든 출판사 조회", description = "전체 출판사 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<List<PublisherInfoResponse>> getAllPublishersPublic() {
        List<PublisherInfoResponse> publishers = publisherService.getAllPublishersPublic();
        return ResponseEntity.ok(publishers);
    }
    
    @Operation(summary = "[Public] 출판사 상세 조회", description = "특정 출판사의 상세 정보를 조회합니다")
    @GetMapping("/{id}")
    public ResponseEntity<PublisherInfoResponse> getPublisherPublic(@PathVariable Long id) {
        PublisherInfoResponse publisher = publisherService.getPublisherByIdPublic(id);
        return ResponseEntity.ok(publisher);
    }
    
    // ========================================
    // 🔒 ADMIN API (ADMIN 권한 필요)
    // ========================================
    
    @Operation(summary = "[Admin] 모든 출판사 조회", description = "전체 출판사 목록을 조회합니다 (ADMIN 전용)")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PublisherInfoResponse>> getAllPublishersAdmin(@AuthUser User admin) {
        List<PublisherInfoResponse> publishers = publisherService.getAllPublishers(admin);
        return ResponseEntity.ok(publishers);
    }
    
    @Operation(summary = "[Admin] 출판사 상세 조회", description = "특정 출판사의 상세 정보를 조회합니다 (ADMIN 전용)")
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherInfoResponse> getPublisherAdmin(
            @AuthUser User admin,
            @PathVariable Long id) {
        PublisherInfoResponse publisher = publisherService.getPublisherById(admin, id);
        return ResponseEntity.ok(publisher);
    }
    
    @Operation(summary = "[Admin] 출판사 생성", description = "새로운 출판사를 등록합니다 (ADMIN 전용)")
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherInfoResponse> createPublisher(
            @AuthUser User admin,
            @Valid @RequestBody PublisherCreateRequest request) {
        PublisherInfoResponse created = publisherService.createPublisher(admin, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @Operation(summary = "[Admin] 출판사 정보 수정", description = "출판사 정보를 수정합니다 (ADMIN 전용)")
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherInfoResponse> updatePublisher(
            @AuthUser User admin,
            @PathVariable Long id,
            @Valid @RequestBody PublisherUpdateRequest request) {
        PublisherInfoResponse updated = publisherService.updatePublisherByAdmin(admin, id, request);
        return ResponseEntity.ok(updated);
    }
    
    @Operation(summary = "[Admin] 출판사 삭제", description = "출판사를 비활성화합니다 (Soft Delete, ADMIN 전용)")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePublisher(
            @AuthUser User admin,
            @PathVariable Long id) {
        publisherService.deletePublisher(admin, id);
        return ResponseEntity.noContent().build();
    }
}
