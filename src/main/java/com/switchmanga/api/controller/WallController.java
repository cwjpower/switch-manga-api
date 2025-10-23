package com.switchmanga.api.controller;

import com.switchmanga.api.entity.TargetType;
import com.switchmanga.api.entity.WallComment;
import com.switchmanga.api.entity.WallLike;
import com.switchmanga.api.entity.WallPost;
import com.switchmanga.api.service.WallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wall")
@RequiredArgsConstructor
public class WallController {

    private final WallService wallService;

    // ==================== 게시글 API ====================

    // 전체 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<List<WallPost>> getAllPosts() {
        List<WallPost> posts = wallService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<WallPost> getPostById(@PathVariable Long id) {
        WallPost post = wallService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // 특정 대상(시리즈/권)의 게시글 조회
    @GetMapping("/posts/target/{targetType}/{targetId}")
    public ResponseEntity<List<WallPost>> getPostsByTarget(
            @PathVariable TargetType targetType,
            @PathVariable Long targetId) {
        List<WallPost> posts = wallService.getPostsByTarget(targetType, targetId);
        return ResponseEntity.ok(posts);
    }

    // 특정 사용자의 게시글 조회
    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<WallPost>> getPostsByUser(@PathVariable Long userId) {
        List<WallPost> posts = wallService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

    // 특정 대상의 게시글 개수
    @GetMapping("/posts/count/{targetType}/{targetId}")
    public ResponseEntity<Long> getPostCountByTarget(
            @PathVariable TargetType targetType,
            @PathVariable Long targetId) {
        Long count = wallService.getPostCountByTarget(targetType, targetId);
        return ResponseEntity.ok(count);
    }

    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<WallPost> createPost(
            @RequestBody WallPost post,
            @RequestParam Long userId) {
        WallPost created = wallService.createPost(post, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 게시글 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<WallPost> updatePost(
            @PathVariable Long id,
            @RequestBody WallPost post) {
        WallPost updated = wallService.updatePost(id, post);
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        wallService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 댓글 API ====================

    // 특정 게시글의 댓글 조회
    @GetMapping("/comments/post/{wallPostId}")
    public ResponseEntity<List<WallComment>> getCommentsByPost(@PathVariable Long wallPostId) {
        List<WallComment> comments = wallService.getCommentsByPost(wallPostId);
        return ResponseEntity.ok(comments);
    }

    // 특정 사용자의 댓글 조회
    @GetMapping("/comments/user/{userId}")
    public ResponseEntity<List<WallComment>> getCommentsByUser(@PathVariable Long userId) {
        List<WallComment> comments = wallService.getCommentsByUser(userId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 상세 조회
    @GetMapping("/comments/{id}")
    public ResponseEntity<WallComment> getCommentById(@PathVariable Long id) {
        WallComment comment = wallService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    // 댓글 작성
    @PostMapping("/comments")
    public ResponseEntity<WallComment> createComment(
            @RequestParam Long wallPostId,
            @RequestBody WallComment comment,
            @RequestParam Long userId) {
        WallComment created = wallService.createComment(wallPostId, comment, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 댓글 수정
    @PutMapping("/comments/{id}")
    public ResponseEntity<WallComment> updateComment(
            @PathVariable Long id,
            @RequestBody WallComment comment) {
        WallComment updated = wallService.updateComment(id, comment);
        return ResponseEntity.ok(updated);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        wallService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 좋아요 API ====================

    // 특정 게시글의 좋아요 개수
    @GetMapping("/likes/count/{wallPostId}")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long wallPostId) {
        Long count = wallService.getLikeCountByPost(wallPostId);
        return ResponseEntity.ok(count);
    }

    // 사용자가 좋아요 눌렀는지 확인
    @GetMapping("/likes/check/{wallPostId}/{userId}")
    public ResponseEntity<Boolean> isLikedByUser(
            @PathVariable Long wallPostId,
            @PathVariable Long userId) {
        boolean isLiked = wallService.isLikedByUser(wallPostId, userId);
        return ResponseEntity.ok(isLiked);
    }

    // 좋아요 추가
    @PostMapping("/likes")
    public ResponseEntity<WallLike> addLike(
            @RequestParam Long wallPostId,
            @RequestParam Long userId) {
        WallLike like = wallService.addLike(wallPostId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(like);
    }

    // 좋아요 취소
    @DeleteMapping("/likes")
    public ResponseEntity<Void> removeLike(
            @RequestParam Long wallPostId,
            @RequestParam Long userId) {
        wallService.removeLike(wallPostId, userId);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 토글 (추가/취소)
    @PostMapping("/likes/toggle")
    public ResponseEntity<Boolean> toggleLike(
            @RequestParam Long wallPostId,
            @RequestParam Long userId) {
        boolean isLiked = wallService.toggleLike(wallPostId, userId);
        return ResponseEntity.ok(isLiked);
    }
}
