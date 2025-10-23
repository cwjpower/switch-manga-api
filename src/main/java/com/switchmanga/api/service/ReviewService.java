package com.switchmanga.api.service;

import com.switchmanga.api.entity.Review;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.repository.ReviewRepository;
import com.switchmanga.api.repository.UserRepository;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VolumeRepository volumeRepository;

    // 권별 리뷰 목록 조회 (최신순)
    public List<Review> getReviewsByVolume(Long volumeId) {
        return reviewRepository.findByVolumeIdOrderByCreatedAtDesc(volumeId);
    }

    // 사용자별 리뷰 목록 조회 (최신순)
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 리뷰 상세 조회
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다. ID: " + id));
    }

    // 권별 리뷰 개수
    public Long getReviewCountByVolume(Long volumeId) {
        return reviewRepository.countByVolumeId(volumeId);
    }

    // 권별 평균 평점
    public Double getAverageRatingByVolume(Long volumeId) {
        Double avgRating = reviewRepository.getAverageRatingByVolumeId(volumeId);
        return avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    }

    // 사용자가 특정 권에 리뷰 작성 여부 확인
    public boolean hasUserReviewedVolume(Long userId, Long volumeId) {
        return reviewRepository.existsByUserIdAndVolumeId(userId, volumeId);
    }

    // 좋아요 많은 순으로 리뷰 조회
    public List<Review> getPopularReviewsByVolume(Long volumeId) {
        return reviewRepository.findByVolumeIdOrderByLikeCountDesc(volumeId);
    }

    // 리뷰 작성
    @Transactional
    public Review createReview(Review review, Long userId, Long volumeId) {
        // User 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        // Volume 존재 확인
        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ID: " + volumeId));

        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByUserIdAndVolumeId(userId, volumeId)) {
            throw new RuntimeException("이미 이 권에 대한 리뷰를 작성하셨습니다.");
        }

        // 평점 유효성 검사 (0.0 ~ 5.0)
        if (review.getRating() < 0.0 || review.getRating() > 5.0) {
            throw new RuntimeException("평점은 0.0에서 5.0 사이여야 합니다.");
        }

        // Review에 User와 Volume 설정
        review.setUser(user);
        review.setVolume(volume);
        review.setLikeCount(0);

        return reviewRepository.save(review);
    }

    // 리뷰 수정
    @Transactional
    public Review updateReview(Long id, Review reviewDetails, Long userId) {
        Review review = getReviewById(id);

        // 작성자 본인 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 평점 유효성 검사
        if (reviewDetails.getRating() != null) {
            if (reviewDetails.getRating() < 0.0 || reviewDetails.getRating() > 5.0) {
                throw new RuntimeException("평점은 0.0에서 5.0 사이여야 합니다.");
            }
            review.setRating(reviewDetails.getRating());
        }

        // 내용 수정
        if (reviewDetails.getContent() != null) {
            review.setContent(reviewDetails.getContent());
        }

        return reviewRepository.save(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long id, Long userId) {
        Review review = getReviewById(id);

        // 작성자 본인 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    // 좋아요 추가
    @Transactional
    public Review addLike(Long id) {
        Review review = getReviewById(id);
        review.setLikeCount(review.getLikeCount() + 1);
        return reviewRepository.save(review);
    }

    // 좋아요 취소
    @Transactional
    public Review removeLike(Long id) {
        Review review = getReviewById(id);
        if (review.getLikeCount() > 0) {
            review.setLikeCount(review.getLikeCount() - 1);
        }
        return reviewRepository.save(review);
    }
}
