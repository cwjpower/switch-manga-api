package com.switchmanga.api.repository;

import com.switchmanga.api.entity.TargetType;
import com.switchmanga.api.entity.WallPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WallPostRepository extends JpaRepository<WallPost, Long> {

    // 특정 대상(시리즈/권)의 담벼락 게시글 조회
    List<WallPost> findByTargetTypeAndTargetId(TargetType targetType, Long targetId);

    // 특정 대상의 담벼락 게시글 조회 (최신순)
    List<WallPost> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(TargetType targetType, Long targetId);

    // 특정 사용자가 작성한 게시글 조회
    List<WallPost> findByUserId(Long userId);

    // 특정 사용자가 작성한 게시글 조회 (최신순)
    List<WallPost> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 대상의 게시글 개수
    Long countByTargetTypeAndTargetId(TargetType targetType, Long targetId);
}
