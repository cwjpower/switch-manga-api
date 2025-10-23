package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Volume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {

    // 시리즈별 Volume 조회
    List<Volume> findBySeriesId(Long seriesId);

    // 시리즈별 + 권수로 조회
    Optional<Volume> findBySeriesIdAndVolumeNumber(Long seriesId, Integer volumeNumber);

    // 활성화된 Volume만 조회
    List<Volume> findByActiveTrue();

    // 시리즈별 + 활성화된 것만
    List<Volume> findBySeriesIdAndActiveTrue(Long seriesId);

    // 제목으로 검색
    List<Volume> findByTitleContaining(String title);

    // ISBN으로 조회
    Optional<Volume> findByIsbn(String isbn);

    // 시리즈별 Volume 개수
    Long countBySeriesId(Long seriesId);

    // 시리즈별 Volume 조회 (권수 순서대로)
    List<Volume> findBySeriesIdOrderByVolumeNumberAsc(Long seriesId);
}