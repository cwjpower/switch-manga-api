package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Page;
import com.switchmanga.api.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    // 1. 권별 페이지 목록 조회
    @GetMapping("/volume/{volumeId}")
    public ResponseEntity<List<Page>> getPagesByVolume(@PathVariable Long volumeId) {
        List<Page> pages = pageService.getPagesByVolume(volumeId);
        return ResponseEntity.ok(pages);
    }

    // 2. 페이지 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Page> getPageById(@PathVariable Long id) {
        Page page = pageService.getPageById(id);
        return ResponseEntity.ok(page);
    }

    // 3. 권별 페이지 개수
    @GetMapping("/volume/{volumeId}/count")
    public ResponseEntity<Long> getPageCountByVolume(@PathVariable Long volumeId) {
        Long count = pageService.getPageCountByVolume(volumeId);
        return ResponseEntity.ok(count);
    }

    // 4. 단일 페이지 생성
    @PostMapping
    public ResponseEntity<Page> createPage(
            @RequestBody Page page,
            @RequestParam Long volumeId) {
        Page created = pageService.createPage(page, volumeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 5. 페이지 수정
    @PutMapping("/{id}")
    public ResponseEntity<Page> updatePage(
            @PathVariable Long id,
            @RequestBody Page page) {
        Page updated = pageService.updatePage(id, page);
        return ResponseEntity.ok(updated);
    }

    // 6. 페이지 순서 변경
    @PutMapping("/{id}/order")
    public ResponseEntity<Page> updatePageOrder(
            @PathVariable Long id,
            @RequestParam Integer pageNumber) {
        Page updated = pageService.updatePageOrder(id, pageNumber);
        return ResponseEntity.ok(updated);
    }

    // 7. 페이지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        pageService.deletePage(id);
        return ResponseEntity.noContent().build();
    }

    // 8. 권별 페이지 전체 삭제
    @DeleteMapping("/volume/{volumeId}")
    public ResponseEntity<Void> deleteAllPagesByVolume(@PathVariable Long volumeId) {
        pageService.deleteAllPagesByVolume(volumeId);
        return ResponseEntity.noContent().build();
    }

    // ⭐ 9. ZIP 파일 업로드 및 페이지 자동 추출 (핵심 기능!)
    @PostMapping(value = "/volume/{volumeId}/upload-zip",
            consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> uploadZip(
            @PathVariable Long volumeId,
            @RequestParam("file") MultipartFile zipFile) {

        try {
            // ZIP 파일 검증
            if (zipFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ZIP 파일이 비어있습니다."));
            }

            if (!zipFile.getOriginalFilename().toLowerCase().endsWith(".zip")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ZIP 파일만 업로드 가능합니다."));
            }

            // ZIP 압축 해제 및 페이지 추출
            List<Page> extractedPages = pageService.uploadZipAndExtractPages(volumeId, zipFile);

            // 응답 데이터 생성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ZIP 파일이 성공적으로 업로드되고 페이지가 추출되었습니다.");
            response.put("volumeId", volumeId);
            response.put("totalPages", extractedPages.size());
            response.put("pages", extractedPages);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "ZIP 파일 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}