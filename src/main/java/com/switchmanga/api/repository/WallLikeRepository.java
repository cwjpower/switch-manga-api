package com.switchmanga.api.repository;

import com.switchmanga.api.entity.WallLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WallLikeRepository extends JpaRepository<WallLike, Long> {

    // 특정 게시글의 좋아요 목록 조회
    List<WallLike> findByWallPostId(Long wallPostId);

    // 특정 사용자가 누른 좋아요 목록 조회
    List<WallLike> findByUserId(Long userId);

    // 특정 게시글에 특정 사용자가 좋아요 눌렀는지 확인
    Optional<WallLike> findByWallPostIdAndUserId(Long wallPostId, Long userId);

    // 특정 게시글에 특정 사용자가 좋아요 눌렀는지 존재 여부
    boolean existsByWallPostIdAndUserId(Long wallPostId, Long userId);

    // 특정 게시글의 좋아요 개수
    Long countByWallPostId(Long wallPostId);

    // 특정 게시글의 좋아요 전체 삭제
    void deleteByWallPostId(Long wallPostId);

    // 특정 게시글에 특정 사용자의 좋아요 삭제
    void deleteByWallPostIdAndUserId(Long wallPostId, Long userId);
}
