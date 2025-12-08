package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Volume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {

    // Series ID로 조회
    List<Volume> findBySeriesId(Long seriesId);

    Page<Volume> findBySeriesId(Long seriesId, Pageable pageable);

    List<Volume> findBySeriesIdOrderByVolumeNumberAsc(Long seriesId);

    // Series의 Publisher ID로 조회 ✅ 수정: SeriesPublisherId → Series_Publisher_Id
    Page<Volume> findBySeries_Publisher_Id(Long publisherId, Pageable pageable);

    List<Volume> findBySeries_Publisher_Id(Long publisherId);

    // 개수 조회
    Long countBySeriesId(Long seriesId);

    Long countBySeries_Publisher_Id(Long publisherId);  // ✅ 수정

    // 특정 권 번호 조회
    Optional<Volume> findBySeriesIdAndVolumeNumber(Long seriesId, Integer volumeNumber);

    // 존재 여부 확인
    boolean existsBySeriesIdAndVolumeNumber(Long seriesId, Integer volumeNumber);

    // 제목 검색
    List<Volume> findByTitleContainingIgnoreCase(String title);

    Page<Volume> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // ISBN으로 검색
    Optional<Volume> findByIsbn(String isbn);

    // ========================================
    // ✅ 검색/필터 메서드 (수정됨)
    // ========================================

    // 시리즈 내 제목 검색
    Page<Volume> findBySeriesIdAndTitleContaining(Long seriesId, String title, Pageable pageable);

    // 시리즈 내 상태 필터
    Page<Volume> findBySeriesIdAndStatus(Long seriesId, String status, Pageable pageable);

    // 시리즈 내 제목 검색 + 상태 필터
    Page<Volume> findBySeriesIdAndTitleContainingAndStatus(
            Long seriesId,
            String title,
            String status,
            Pageable pageable
    );

    // 전체 제목 검색 (Publisher 기준) ✅ 수정
    Page<Volume> findBySeries_Publisher_IdAndTitleContaining(
            Long publisherId,
            String title,
            Pageable pageable
    );

    // 전체 상태 필터 (Publisher 기준) ✅ 수정
    Page<Volume> findBySeries_Publisher_IdAndStatus(
            Long publisherId,
            String status,
            Pageable pageable
    );

    // 전체 제목 검색 + 상태 필터 (Publisher 기준) ✅ 수정
    Page<Volume> findBySeries_Publisher_IdAndTitleContainingAndStatus(
            Long publisherId,
            String title,
            String status,
            Pageable pageable
    );
}