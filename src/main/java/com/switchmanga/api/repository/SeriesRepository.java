package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    // Publisher ID로 조회 ✅ 수정: PublisherId → Publisher_Id
    Page<Series> findByPublisher_Id(Long publisherId, Pageable pageable);

    List<Series> findByPublisher_Id(Long publisherId);

    // Publisher ID + Status로 조회 ✅ 수정
    Page<Series> findByPublisher_IdAndStatus(Long publisherId, String status, Pageable pageable);

    // Publisher ID + 제목 검색 ✅ 수정
    Page<Series> findByPublisher_IdAndTitleContainingIgnoreCase(Long publisherId, String title, Pageable pageable);

    // Publisher ID로 개수 조회 ✅ 수정
    Long countByPublisher_Id(Long publisherId);

    // 제목으로 검색
    List<Series> findByTitleContainingIgnoreCase(String title);

    Page<Series> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 작가로 검색
    List<Series> findByAuthorContainingIgnoreCase(String author);

    // Status로 조회
    List<Series> findByStatus(String status);

    Page<Series> findByStatus(String status, Pageable pageable);
}