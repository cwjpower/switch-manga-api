package com.switchmanga.api.controller;

import com.switchmanga.api.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    // 전체 시리즈 조회
    @GetMapping
    public ResponseEntity<List<Series>> getAllSeries() {
        List<Series> series = seriesService.getAllSeries();
        return ResponseEntity.ok(series);
    }

    // 시리즈 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Series> getSeriesById(@PathVariable Long id) {
        Series series = seriesService.getSeriesById(id);
        return ResponseEntity.ok(series);
    }

    // 출판사별 시리즈 조회
    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<List<Series>> getSeriesByPublisher(@PathVariable Long publisherId) {
        List<Series> series = seriesService.getSeriesByPublisher(publisherId);
        return ResponseEntity.ok(series);
    }

    // 활성화된 시리즈만 조회
    @GetMapping("/active")
    public ResponseEntity<List<Series>> getActiveSeries() {
        List<Series> series = seriesService.getActiveSeries();
        return ResponseEntity.ok(series);
    }

    // 상태별 시리즈 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Series>> getSeriesByStatus(@PathVariable String status) {
        List<Series> series = seriesService.getSeriesByStatus(status);
        return ResponseEntity.ok(series);
    }

    // 제목으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<Series>> searchSeriesByTitle(@RequestParam String title) {
        List<Series> series = seriesService.searchSeriesByTitle(title);
        return ResponseEntity.ok(series);
    }

    // 작가로 검색
    @GetMapping("/search/author")
    public ResponseEntity<List<Series>> searchSeriesByAuthor(@RequestParam String author) {
        List<Series> series = seriesService.searchSeriesByAuthor(author);
        return ResponseEntity.ok(series);
    }

    // 시리즈 생성
    @PostMapping
    public ResponseEntity<Series> createSeries(
            @RequestBody Series series,
            @RequestParam Long publisherId) {
        Series created = seriesService.createSeries(series, publisherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 시리즈 수정
    @PutMapping("/{id}")
    public ResponseEntity<Series> updateSeries(
            @PathVariable Long id,
            @RequestBody Series series) {
        Series updated = seriesService.updateSeries(id, series);
        return ResponseEntity.ok(updated);
    }

    // 출판사 변경
    @PatchMapping("/{id}/publisher")
    public ResponseEntity<Series> changePublisher(
            @PathVariable Long id,
            @RequestParam Long publisherId) {
        Series updated = seriesService.changePublisher(id, publisherId);
        return ResponseEntity.ok(updated);
    }

    // 시리즈 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }
}