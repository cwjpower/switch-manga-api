package com.switchmanga.api.repository;

import com.switchmanga.api.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    // 리뷰 ID로 답글 조회
    Optional<ReviewReply> findByReviewId(Long reviewId);

    // 리뷰 ID로 답글 존재 여부 확인
    boolean existsByReviewId(Long reviewId);

    // 출판사 ID로 모든 답글 조회
    List<ReviewReply> findByPublisherId(Long publisherId);

    // 출판사의 답글 수 카운트
    long countByPublisherId(Long publisherId);

    // 리뷰 ID 목록으로 답글 조회 (N+1 방지)
    @Query("SELECT rr FROM ReviewReply rr WHERE rr.review.id IN :reviewIds")
    List<ReviewReply> findByReviewIdIn(@Param("reviewIds") List<Long> reviewIds);

    // 리뷰 ID로 답글 삭제
    void deleteByReviewId(Long reviewId);
}
