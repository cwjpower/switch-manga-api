package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Volume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Volume Repository
 * 권(Volume) 관련 데이터베이스 접근
 */
@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {

    /**
     * 시리즈별 권 목록 조회 (권 번호 순)
     */
    List<Volume> findBySeriesIdOrderByVolumeNumberAsc(Long seriesId);

    /**
     * 시리즈별 + 활성화 권 목록
     */
    List<Volume> findBySeriesIdAndActiveOrderByVolumeNumberAsc(Long seriesId, Boolean active);

    /**
     * 시리즈별 권 수
     */
    Long countBySeriesId(Long seriesId);

    /**
     * 시리즈별 활성화된 권 수
     */
    Long countBySeriesIdAndActive(Long seriesId, Boolean active);

    /**
     * 출판사별 권 목록 조회
     * Volume → Series → Publisher 관계 추적
     */
    @Query("SELECT v FROM Volume v JOIN v.series s WHERE s.publisher.id = :publisherId ORDER BY v.createdAt DESC")
    List<Volume> findByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 출판사별 권 수
     */
    @Query("SELECT COUNT(v) FROM Volume v JOIN v.series s WHERE s.publisher.id = :publisherId")
    Long countByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 출판사별 + 활성화 권 수
     */
    @Query("SELECT COUNT(v) FROM Volume v JOIN v.series s WHERE s.publisher.id = :publisherId AND v.active = :active")
    Long countByPublisherIdAndActive(@Param("publisherId") Long publisherId, @Param("active") Boolean active);

    /**
     * 시리즈별 특정 권 번호 조회
     */
    Optional<Volume> findBySeriesIdAndVolumeNumber(Long seriesId, Integer volumeNumber);

    /**
     * ISBN으로 조회
     */
    Optional<Volume> findByIsbn(String isbn);

    /**
     * 제목으로 검색 (부분 일치)
     */
    List<Volume> findByTitleContainingIgnoreCase(String title);

    /**
     * 가격 범위로 검색
     */
    @Query("SELECT v FROM Volume v WHERE v.price BETWEEN :minPrice AND :maxPrice AND v.active = true")
    List<Volume> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * 인기 권 Top N (조회수 기준)
     */
    @Query("SELECT v FROM Volume v WHERE v.active = true ORDER BY v.viewCount DESC")
    List<Volume> findTopByOrderByViewCountDesc();

    /**
     * 평점 높은 권 Top N
     */
    @Query("SELECT v FROM Volume v WHERE v.active = true AND v.averageRating IS NOT NULL ORDER BY v.averageRating DESC")
    List<Volume> findTopByOrderByAverageRatingDesc();

    /**
     * 최신 권 조회
     */
    @Query("SELECT v FROM Volume v WHERE v.active = true ORDER BY v.createdAt DESC")
    List<Volume> findTopByOrderByCreatedAtDesc();

    /**
     * 시리즈의 최신 권 조회
     */
    @Query("SELECT v FROM Volume v WHERE v.series.id = :seriesId AND v.active = true ORDER BY v.volumeNumber DESC")
    List<Volume> findLatestBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * 출판사별 인기 권
     */
    @Query("SELECT v FROM Volume v JOIN v.series s WHERE s.publisher.id = :publisherId AND v.active = true ORDER BY v.viewCount DESC")
    List<Volume> findTopByPublisherIdOrderByViewCountDesc(@Param("publisherId") Long publisherId);

    /**
     * 시리즈의 첫 번째 권 조회
     */
    Optional<Volume> findFirstBySeriesIdOrderByVolumeNumberAsc(Long seriesId);

    /**
     * 시리즈의 마지막 권 조회
     */
    Optional<Volume> findFirstBySeriesIdOrderByVolumeNumberDesc(Long seriesId);

    /**
     * Volume ID와 출판사 ID로 조회 (권한 확인용)
     */
    @Query("SELECT v FROM Volume v JOIN v.series s WHERE v.id = :volumeId AND s.publisher.id = :publisherId")
    Optional<Volume> findByIdAndPublisherId(@Param("volumeId") Long volumeId, @Param("publisherId") Long publisherId);

    /**
     * 활성화된 권만 조회
     */
    List<Volume> findByActive(Boolean active);

    /**
     * 특정 출판일 이후의 권 조회
     */
    @Query("SELECT v FROM Volume v WHERE v.publishDate >= :date AND v.active = true ORDER BY v.publishDate DESC")
    List<Volume> findByPublishDateAfter(@Param("date") java.time.LocalDate date);
}
