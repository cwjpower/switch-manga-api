package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    // 전체 출판사 조회
    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        List<Publisher> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(publishers);
    }

    // 출판사 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        Publisher publisher = publisherService.getPublisherById(id);
        return ResponseEntity.ok(publisher);
    }

    // 활성화된 출판사만 조회
    @GetMapping("/active")
    public ResponseEntity<List<Publisher>> getActivePublishers() {
        List<Publisher> publishers = publisherService.getActivePublishers();
        return ResponseEntity.ok(publishers);
    }

    // 국가별 출판사 조회
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Publisher>> getPublishersByCountry(@PathVariable String country) {
        List<Publisher> publishers = publisherService.getPublishersByCountry(country);
        return ResponseEntity.ok(publishers);
    }

    // 출판사 생성
    @PostMapping
    public ResponseEntity<Publisher> createPublisher(@RequestBody Publisher publisher) {
        Publisher created = publisherService.createPublisher(publisher);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 출판사 수정
    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(
            @PathVariable Long id,
            @RequestBody Publisher publisher) {
        Publisher updated = publisherService.updatePublisher(id, publisher);
        return ResponseEntity.ok(updated);
    }

    // 출판사 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}