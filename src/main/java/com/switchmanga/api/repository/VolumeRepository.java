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

    // Series의 Publisher ID로 조회
    Page<Volume> findBySeriesPublisherId(Long publisherId, Pageable pageable);

    List<Volume> findBySeriesPublisherId(Long publisherId);

    // 개수 조회
    Long countBySeriesId(Long seriesId);

    Long countBySeriesPublisherId(Long publisherId);

    // 특정 권 번호 조회
    Optional<Volume> findBySeriesIdAndVolumeNumber(Long seriesId, Integer volumeNumber);

    // 존재 여부 확인
    boolean existsBySeriesIdAndVolumeNumber(Long seriesId, Integer volumeNumber);

    // 제목 검색
    List<Volume> findByTitleContainingIgnoreCase(String title);

    Page<Volume> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // ISBN으로 검색
    Optional<Volume> findByIsbn(String isbn);
}