package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publishers")
@RequiredArgsConstructor
@Tag(name = "Publisher", description = "출판사 관리 API - 출판사 등록, 조회, 수정, 삭제 기능을 제공합니다")
public class PublisherController {

    private final PublisherService publisherService;

    @Operation(
        summary = "전체 출판사 조회",
        description = "시스템에 등록된 모든 출판사 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        List<Publisher> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(publishers);
    }

    @Operation(
        summary = "출판사 상세 조회",
        description = "특정 출판사의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "출판사를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(
        @Parameter(description = "출판사 ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        Publisher publisher = publisherService.getPublisherById(id);
        return ResponseEntity.ok(publisher);
    }

    @Operation(
        summary = "활성화된 출판사 조회",
        description = "활성화된 출판사 목록만 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/active")
    public ResponseEntity<List<Publisher>> getActivePublishers() {
        List<Publisher> publishers = publisherService.getActivePublishers();
        return ResponseEntity.ok(publishers);
    }

    @Operation(
        summary = "국가별 출판사 조회",
        description = "특정 국가의 출판사 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Publisher>> getPublishersByCountry(
        @Parameter(description = "국가 코드", required = true, example = "JP")
        @PathVariable String country
    ) {
        List<Publisher> publishers = publisherService.getPublishersByCountry(country);
        return ResponseEntity.ok(publishers);
    }

    @Operation(
        summary = "출판사 생성",
        description = "새로운 출판사를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "출판사 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Publisher> createPublisher(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "출판사 정보",
            required = true
        )
        @RequestBody Publisher publisher
    ) {
        Publisher created = publisherService.createPublisher(publisher);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "출판사 수정",
        description = "출판사 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "출판사를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(
        @Parameter(description = "출판사 ID", required = true, example = "1")
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "수정할 출판사 정보",
            required = true
        )
        @RequestBody Publisher publisher
    ) {
        Publisher updated = publisherService.updatePublisher(id, publisher);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "출판사 삭제",
        description = "출판사를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "출판사를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(
        @Parameter(description = "출판사 ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
