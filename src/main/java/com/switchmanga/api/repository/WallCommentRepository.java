package com.switchmanga.api.repository;

import com.switchmanga.api.entity.WallComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WallCommentRepository extends JpaRepository<WallComment, Long> {

    // 특정 게시글의 댓글 조회
    List<WallComment> findByWallPostId(Long wallPostId);

    // 특정 게시글의 댓글 조회 (최신순)
    List<WallComment> findByWallPostIdOrderByCreatedAtDesc(Long wallPostId);

    // 특정 게시글의 댓글 조회 (오래된 순)
    List<WallComment> findByWallPostIdOrderByCreatedAtAsc(Long wallPostId);

    // 특정 사용자가 작성한 댓글 조회
    List<WallComment> findByUserId(Long userId);

    // 특정 사용자가 작성한 댓글 조회 (최신순)
    List<WallComment> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 게시글의 댓글 개수
    Long countByWallPostId(Long wallPostId);

    // 특정 게시글의 댓글 전체 삭제
    void deleteByWallPostId(Long wallPostId);
}
