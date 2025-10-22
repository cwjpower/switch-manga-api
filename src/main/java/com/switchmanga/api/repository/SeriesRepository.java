package com.switchmanga.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    // 출판사별 시리즈 조회
    List<Series> findByPublisherId(Long publisherId);

    // 활성화된 시리즈만 조회
    List<Series> findByActiveTrue();

    // 상태별 조회 (ongoing, completed, hiatus)
    List<Series> findByStatus(String status);

    // 제목으로 검색 (부분 일치)
    List<Series> findByTitleContaining(String title);

    // 작가로 검색
    List<Series> findByAuthorContaining(String author);

    // 출판사 + 상태 조회
    List<Series> findByPublisherIdAndStatus(Long publisherId, String status);
}
