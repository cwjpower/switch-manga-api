package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    // Publisher ID로 조회
    Page<Series> findByPublisherId(Long publisherId, Pageable pageable);

    List<Series> findByPublisherId(Long publisherId);

    // Publisher ID + Status로 조회
    Page<Series> findByPublisherIdAndStatus(Long publisherId, String status, Pageable pageable);

    // Publisher ID + 제목 검색
    Page<Series> findByPublisherIdAndTitleContainingIgnoreCase(Long publisherId, String title, Pageable pageable);

    // Publisher ID로 개수 조회
    Long countByPublisherId(Long publisherId);

    // 제목으로 검색
    List<Series> findByTitleContainingIgnoreCase(String title);

    Page<Series> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 작가로 검색
    List<Series> findByAuthorContainingIgnoreCase(String author);

    // Status로 조회
    List<Series> findByStatus(String status);

    Page<Series> findByStatus(String status, Pageable pageable);
}