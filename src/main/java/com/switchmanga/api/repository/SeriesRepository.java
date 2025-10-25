package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Series Repository
 * 시리즈 관련 데이터베이스 접근
 */
@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    /**
     * 출판사별 시리즈 목록 조회
     */
    List<Series> findByPublisherId(Long publisherId);

    /**
     * 출판사별 + 활성화 시리즈 목록
     */
    List<Series> findByPublisherIdAndActive(Long publisherId, Boolean active);

    /**
     * 출판사별 시리즈 수 조회
     */
    Long countByPublisherId(Long publisherId);

    /**
     * 출판사별 활성화된 시리즈 수
     */
    Long countByPublisherIdAndActive(Long publisherId, Boolean active);

    /**
     * 활성화 상태별 시리즈 조회 (추가!)
     */
    List<Series> findByActive(Boolean active);

    /**
     * 카테고리별 시리즈 조회
     */
    List<Series> findByCategoryId(Long categoryId);

    /**
     * 카테고리별 + 활성화 시리즈
     */
    List<Series> findByCategoryIdAndActive(Long categoryId, Boolean active);

    /**
     * 제목으로 검색 (부분 일치)
     */
    List<Series> findByTitleContainingIgnoreCase(String title);

    /**
     * 작가로 검색
     */
    List<Series> findByAuthorContainingIgnoreCase(String author);

    /**
     * 상태별 시리즈 조회
     */
    List<Series> findByStatus(String status);

    /**
     * 출판사별 + 상태별 시리즈
     */
    List<Series> findByPublisherIdAndStatus(Long publisherId, String status);

    /**
     * 인기 시리즈 Top N (조회수 기준)
     */
    @Query("SELECT s FROM Series s WHERE s.active = true ORDER BY s.viewCount DESC")
    List<Series> findTopByOrderByViewCountDesc();

    /**
     * 평점 높은 시리즈 Top N
     */
    @Query("SELECT s FROM Series s WHERE s.active = true AND s.averageRating IS NOT NULL ORDER BY s.averageRating DESC, s.reviewCount DESC")
    List<Series> findTopByOrderByAverageRatingDesc();

    /**
     * 최신 시리즈 조회
     */
    @Query("SELECT s FROM Series s WHERE s.active = true ORDER BY s.createdAt DESC")
    List<Series> findTopByOrderByCreatedAtDesc();

    /**
     * 출판사별 인기 시리즈
     */
    @Query("SELECT s FROM Series s WHERE s.publisher.id = :publisherId AND s.active = true ORDER BY s.viewCount DESC")
    List<Series> findTopByPublisherIdOrderByViewCountDesc(@Param("publisherId") Long publisherId);

    /**
     * 완결된 시리즈 조회
     */
    List<Series> findByStatusAndActive(String status, Boolean active);

    /**
     * 시리즈 ID와 출판사 ID로 조회 (권한 확인용)
     */
    Optional<Series> findByIdAndPublisherId(Long id, Long publisherId);
}
