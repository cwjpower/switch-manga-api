// src/main/java/com/switchmanga/api/repository/VolumeRepository.java

package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Volume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Volume Repository
 */
@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {
    
    /**
     * Series ID로 Volume 개수 조회
     */
    Long countBySeriesId(Long seriesId);
    
    /**
     * Publisher ID로 Volume 개수 조회
     * Series를 통해 Publisher와 연결
     */
    @Query("SELECT COUNT(v) FROM Volume v WHERE v.series.publisher.id = :publisherId")
    Long countByPublisherId(@Param("publisherId") Long publisherId);
    
    /**
     * Series ID로 Volume 목록 조회 (페이징)
     */
    Page<Volume> findBySeriesId(Long seriesId, Pageable pageable);
    
    /**
     * Publisher ID로 Volume 목록 조회 (페이징)
     */
    @Query("SELECT v FROM Volume v WHERE v.series.publisher.id = :publisherId")
    Page<Volume> findByPublisherId(@Param("publisherId") Long publisherId, Pageable pageable);
}
