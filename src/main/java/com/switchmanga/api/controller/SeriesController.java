package com.switchmanga.api.controller;

import com.switchmanga.api.entity.Series;
import com.switchmanga.api.service.SeriesService;
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
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
@Tag(name = "Series", description = "시리즈 관리 API - 만화 시리즈의 등록, 조회, 수정, 삭제 기능을 제공합니다")
public class SeriesController {

    private final SeriesService seriesService;

    @Operation(
        summary = "전체 시리즈 조회",
        description = "시스템에 등록된 모든 시리즈를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<Series>> getAllSeries() {
        List<Series> series = seriesService.getAllSeries();
        return ResponseEntity.ok(series);
    }

    @Operation(
        summary = "시리즈 상세 조회",
        description = "특정 시리즈의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "시리즈를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Series> getSeriesById(
        @Parameter(description = "시리즈 ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        Series series = seriesService.getSeriesById(id);
        return ResponseEntity.ok(series);
    }

    @Operation(
        summary = "출판사별 시리즈 조회",
        description = "특정 출판사의 모든 시리즈를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<List<Series>> getSeriesByPublisher(
        @Parameter(description = "출판사 ID", required = true, example = "1")
        @PathVariable Long publisherId
    ) {
        List<Series> series = seriesService.getSeriesByPublisher(publisherId);
        return ResponseEntity.ok(series);
    }

    @Operation(
        summary = "활성화된 시리즈 조회",
        description = "활성화된 시리즈 목록만 조회합니다."
    )
    @GetMapping("/active")
    public ResponseEntity<List<Series>> getActiveSeries() {
        List<Series> series = seriesService.getActiveSeries();
        return ResponseEntity.ok(series);
    }

    @Operation(
        summary = "상태별 시리즈 조회",
        description = "특정 상태(연재중/완결/중단)의 시리즈를 조회합니다."
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Series>> getSeriesByStatus(
        @Parameter(description = "시리즈 상태", required = true, example = "ONGOING")
        @PathVariable String status
    ) {
        List<Series> series = seriesService.getSeriesByStatus(status);
        return ResponseEntity.ok(series);
    }

    @Operation(
        summary = "제목으로 시리즈 검색",
        description = "제목으로 시리즈를 검색합니다. (부분 일치)"
    )
    @GetMapping("/search")
    public ResponseEntity<List<Series>> searchSeriesByTitle(
        @Parameter(description = "검색할 제목", required = true, example = "원피스")
        @RequestParam String title
    ) {
        List<Series> series = seriesService.searchSeriesByTitle(title);
        return ResponseEntity.ok(series);
    }

    @Operation(
        summary = "작가명으로 시리즈 검색",
        description = "작가명으로 시리즈를 검색합니다. (부분 일치)"
    )
    @GetMapping("/search/author")
    public ResponseEntity<List<Series>> searchSeriesByAuthor(
        @Parameter(description = "검색할 작가명", required = true, example = "오다")
        @RequestParam String author
    ) {
        List<Series> series = seriesService.searchSeriesByAuthor(author);
        return ResponseEntity.ok(series);
    }

    @Operation(
        summary = "시리즈 생성",
        description = "새로운 시리즈를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "시리즈 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Series> createSeries(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "시리즈 정보",
            required = true
        )
        @RequestBody Series series,
        @Parameter(description = "출판사 ID", required = true, example = "1")
        @RequestParam Long publisherId
    ) {
        Series created = seriesService.createSeries(series, publisherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "시리즈 수정",
        description = "시리즈 정보를 수정합니다."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Series> updateSeries(
        @Parameter(description = "시리즈 ID", required = true)
        @PathVariable Long id,
        @RequestBody Series series
    ) {
        Series updated = seriesService.updateSeries(id, series);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "출판사 변경",
        description = "시리즈의 출판사를 변경합니다."
    )
    @PatchMapping("/{id}/publisher")
    public ResponseEntity<Series> changePublisher(
        @Parameter(description = "시리즈 ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "새 출판사 ID", required = true)
        @RequestParam Long publisherId
    ) {
        Series updated = seriesService.changePublisher(id, publisherId);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "시리즈 삭제",
        description = "시리즈를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "시리즈를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeries(
        @Parameter(description = "시리즈 ID", required = true)
        @PathVariable Long id
    ) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }
}
