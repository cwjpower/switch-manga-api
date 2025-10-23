package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Series;
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

    @GetMapping
    public ResponseEntity<List<Series>> getAllSeries() {
        List<Series> series = seriesService.getAllSeries();
        return ResponseEntity.ok(series);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Series> getSeriesById(@PathVariable Long id) {
        Series series = seriesService.getSeriesById(id);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<List<Series>> getSeriesByPublisher(@PathVariable Long publisherId) {
        List<Series> series = seriesService.getSeriesByPublisher(publisherId);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Series>> getActiveSeries() {
        List<Series> series = seriesService.getActiveSeries();
        return ResponseEntity.ok(series);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Series>> getSeriesByStatus(@PathVariable String status) {
        List<Series> series = seriesService.getSeriesByStatus(status);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Series>> searchSeriesByTitle(@RequestParam String title) {
        List<Series> series = seriesService.searchSeriesByTitle(title);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<Series>> searchSeriesByAuthor(@RequestParam String author) {
        List<Series> series = seriesService.searchSeriesByAuthor(author);
        return ResponseEntity.ok(series);
    }

    @PostMapping
    public ResponseEntity<Series> createSeries(
            @RequestBody Series series,
            @RequestParam Long publisherId) {
        Series created = seriesService.createSeries(series, publisherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Series> updateSeries(
            @PathVariable Long id,
            @RequestBody Series series) {
        Series updated = seriesService.updateSeries(id, series);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/publisher")
    public ResponseEntity<Series> changePublisher(
            @PathVariable Long id,
            @RequestParam Long publisherId) {
        Series updated = seriesService.changePublisher(id, publisherId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }
}