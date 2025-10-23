package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.service.VolumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/volumes")
@RequiredArgsConstructor
public class VolumeController {

    private final VolumeService volumeService;

    // 전체 Volume 조회
    @GetMapping
    public ResponseEntity<List<Volume>> getAllVolumes() {
        List<Volume> volumes = volumeService.getAllVolumes();
        return ResponseEntity.ok(volumes);
    }

    // Volume 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Volume> getVolumeById(@PathVariable Long id) {
        Volume volume = volumeService.getVolumeById(id);
        return ResponseEntity.ok(volume);
    }

    // 시리즈별 Volume 조회
    @GetMapping("/series/{seriesId}")
    public ResponseEntity<List<Volume>> getVolumesBySeries(@PathVariable Long seriesId) {
        List<Volume> volumes = volumeService.getVolumesBySeries(seriesId);
        return ResponseEntity.ok(volumes);
    }

    // 시리즈별 Volume 조회 (권수 순서대로)
    @GetMapping("/series/{seriesId}/ordered")
    public ResponseEntity<List<Volume>> getVolumesBySeriesOrdered(@PathVariable Long seriesId) {
        List<Volume> volumes = volumeService.getVolumesBySeriesOrdered(seriesId);
        return ResponseEntity.ok(volumes);
    }

    // 특정 시리즈의 특정 권 조회
    @GetMapping("/series/{seriesId}/number/{volumeNumber}")
    public ResponseEntity<Volume> getVolumeBySeriesAndNumber(
            @PathVariable Long seriesId,
            @PathVariable Integer volumeNumber) {
        Volume volume = volumeService.getVolumeBySeriesAndNumber(seriesId, volumeNumber);
        return ResponseEntity.ok(volume);
    }

    // 활성화된 Volume만 조회
    @GetMapping("/active")
    public ResponseEntity<List<Volume>> getActiveVolumes() {
        List<Volume> volumes = volumeService.getActiveVolumes();
        return ResponseEntity.ok(volumes);
    }

    // 시리즈별 활성 Volume 조회
    @GetMapping("/series/{seriesId}/active")
    public ResponseEntity<List<Volume>> getActiveVolumesBySeries(@PathVariable Long seriesId) {
        List<Volume> volumes = volumeService.getActiveVolumesBySeries(seriesId);
        return ResponseEntity.ok(volumes);
    }

    // 제목으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<Volume>> searchVolumesByTitle(@RequestParam String title) {
        List<Volume> volumes = volumeService.searchVolumesByTitle(title);
        return ResponseEntity.ok(volumes);
    }

    // ISBN으로 조회
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Volume> getVolumeByIsbn(@PathVariable String isbn) {
        Volume volume = volumeService.getVolumeByIsbn(isbn);
        return ResponseEntity.ok(volume);
    }

    // 시리즈별 Volume 개수
    @GetMapping("/series/{seriesId}/count")
    public ResponseEntity<Long> getVolumeCountBySeries(@PathVariable Long seriesId) {
        Long count = volumeService.getVolumeCountBySeries(seriesId);
        return ResponseEntity.ok(count);
    }

    // Volume 생성
    @PostMapping
    public ResponseEntity<Volume> createVolume(
            @RequestBody Volume volume,
            @RequestParam Long seriesId) {
        Volume created = volumeService.createVolume(volume, seriesId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Volume 수정
    @PutMapping("/{id}")
    public ResponseEntity<Volume> updateVolume(
            @PathVariable Long id,
            @RequestBody Volume volume) {
        Volume updated = volumeService.updateVolume(id, volume);
        return ResponseEntity.ok(updated);
    }

    // 시리즈 변경
    @PatchMapping("/{id}/series")
    public ResponseEntity<Volume> changeSeries(
            @PathVariable Long id,
            @RequestParam Long seriesId) {
        Volume updated = volumeService.changeSeries(id, seriesId);
        return ResponseEntity.ok(updated);
    }

    // Volume 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolume(@PathVariable Long id) {
        volumeService.deleteVolume(id);
        return ResponseEntity.noContent().build();
    }
}