package com.switchmanga.api.service;

import com.switchmanga.api.entity.*;
import com.switchmanga.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WallService {

    private final WallPostRepository wallPostRepository;
    private final WallCommentRepository wallCommentRepository;
    private final WallLikeRepository wallLikeRepository;
    private final UserRepository userRepository;

    // ==================== 게시글 관리 ====================

    // 게시글 전체 조회
    public List<WallPost> getAllPosts() {
        return wallPostRepository.findAll();
    }

    // 게시글 상세 조회
    public WallPost getPostById(Long id) {
        return wallPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    // 특정 대상(시리즈/권)의 게시글 조회
    public List<WallPost> getPostsByTarget(TargetType targetType, Long targetId) {
        return wallPostRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId);
    }

    // 특정 사용자의 게시글 조회
    public List<WallPost> getPostsByUser(Long userId) {
        return wallPostRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 특정 대상의 게시글 개수
    public Long getPostCountByTarget(TargetType targetType, Long targetId) {
        return wallPostRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    // 게시글 작성
    @Transactional
    public WallPost createPost(WallPost post, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        post.setUser(user);
        post.setLikeCount(0);
        post.setCommentCount(0);

        return wallPostRepository.save(post);
    }

    // 게시글 수정
    @Transactional
    public WallPost updatePost(Long id, WallPost postDetails) {
        WallPost post = getPostById(id);

        post.setContent(postDetails.getContent());

        return wallPostRepository.save(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        WallPost post = getPostById(id);

        // 관련 댓글 삭제
        wallCommentRepository.deleteByWallPostId(id);

        // 관련 좋아요 삭제
        wallLikeRepository.deleteByWallPostId(id);

        // 게시글 삭제
        wallPostRepository.delete(post);
    }

    // ==================== 댓글 관리 ====================

    // 특정 게시글의 댓글 조회 (오래된 순)
    public List<WallComment> getCommentsByPost(Long wallPostId) {
        return wallCommentRepository.findByWallPostIdOrderByCreatedAtAsc(wallPostId);
    }

    // 특정 사용자의 댓글 조회
    public List<WallComment> getCommentsByUser(Long userId) {
        return wallCommentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 댓글 상세 조회
    public WallComment getCommentById(Long id) {
        return wallCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다. ID: " + id));
    }

    // 댓글 작성
    @Transactional
    public WallComment createComment(Long wallPostId, WallComment comment, Long userId) {
        WallPost post = getPostById(wallPostId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        comment.setWallPost(post);
        comment.setUser(user);

        WallComment savedComment = wallCommentRepository.save(comment);

        // 게시글의 댓글 수 증가
        post.setCommentCount(post.getCommentCount() + 1);
        wallPostRepository.save(post);

        return savedComment;
    }

    // 댓글 수정
    @Transactional
    public WallComment updateComment(Long id, WallComment commentDetails) {
        WallComment comment = getCommentById(id);

        comment.setContent(commentDetails.getContent());

        return wallCommentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        WallComment comment = getCommentById(id);
        WallPost post = comment.getWallPost();

        wallCommentRepository.delete(comment);

        // 게시글의 댓글 수 감소
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        wallPostRepository.save(post);
    }

    // ==================== 좋아요 관리 ====================

    // 특정 게시글의 좋아요 개수
    public Long getLikeCountByPost(Long wallPostId) {
        return wallLikeRepository.countByWallPostId(wallPostId);
    }

    // 사용자가 좋아요 눌렀는지 확인
    public boolean isLikedByUser(Long wallPostId, Long userId) {
        return wallLikeRepository.existsByWallPostIdAndUserId(wallPostId, userId);
    }

    // 좋아요 추가
    @Transactional
    public WallLike addLike(Long wallPostId, Long userId) {
        // 이미 좋아요 눌렀는지 확인
        if (isLikedByUser(wallPostId, userId)) {
            throw new RuntimeException("이미 좋아요를 누른 게시글입니다.");
        }

        WallPost post = getPostById(wallPostId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        WallLike like = new WallLike();
        like.setWallPost(post);
        like.setUser(user);

        WallLike savedLike = wallLikeRepository.save(like);

        // 게시글의 좋아요 수 증가
        post.setLikeCount(post.getLikeCount() + 1);
        wallPostRepository.save(post);

        return savedLike;
    }

    // 좋아요 취소
    @Transactional
    public void removeLike(Long wallPostId, Long userId) {
        WallLike like = wallLikeRepository.findByWallPostIdAndUserId(wallPostId, userId)
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));

        WallPost post = like.getWallPost();

        wallLikeRepository.delete(like);

        // 게시글의 좋아요 수 감소
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        wallPostRepository.save(post);
    }

    // 좋아요 토글 (있으면 취소, 없으면 추가)
    @Transactional
    public boolean toggleLike(Long wallPostId, Long userId) {
        if (isLikedByUser(wallPostId, userId)) {
            removeLike(wallPostId, userId);
            return false; // 좋아요 취소
        } else {
            addLike(wallPostId, userId);
            return true; // 좋아요 추가
        }
    }
}
