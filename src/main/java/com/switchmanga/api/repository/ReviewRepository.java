package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 권별 리뷰 목록 조회
    List<Review> findByVolumeId(Long volumeId);

    // 권별 리뷰 목록 조회 (최신순)
    List<Review> findByVolumeIdOrderByCreatedAtDesc(Long volumeId);

    // 사용자별 리뷰 목록 조회
    List<Review> findByUserId(Long userId);

    // 사용자별 리뷰 목록 조회 (최신순)
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 사용자가 특정 권에 작성한 리뷰 조회
    Optional<Review> findByUserIdAndVolumeId(Long userId, Long volumeId);

    // 권별 리뷰 개수
    Long countByVolumeId(Long volumeId);

    // 권별 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.volume.id = :volumeId")
    Double getAverageRatingByVolumeId(Long volumeId);

    // 사용자가 특정 권에 리뷰를 작성했는지 확인
    boolean existsByUserIdAndVolumeId(Long userId, Long volumeId);

    // 평점별 리뷰 조회
    List<Review> findByVolumeIdAndRating(Long volumeId, Double rating);

    // 좋아요 많은 순으로 리뷰 조회
    List<Review> findByVolumeIdOrderByLikeCountDesc(Long volumeId);
}
