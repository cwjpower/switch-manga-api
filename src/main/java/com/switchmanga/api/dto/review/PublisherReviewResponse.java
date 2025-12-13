package com.switchmanga.api.dto.review;

import java.time.LocalDateTime;

/**
 * 출판사용 리뷰 응답 DTO
 */
public class PublisherReviewResponse {

    // ==================== 리뷰 기본 정보 ====================
    private Long id;
    private Double rating;  // Double로 변경!
    private String content;
    private Integer likeCount;
    private Boolean isHidden;
    private LocalDateTime createdAt;

    // ==================== 작성자 정보 ====================
    private Long userId;
    private String userName;
    private String userProfileImage;

    // ==================== 콘텐츠 정보 ====================
    private Long seriesId;
    private String seriesTitle;
    private Long volumeId;
    private String volumeTitle;
    private String volumeCoverImage;

    // ==================== 답글 정보 ====================
    private ReplyInfo reply;

    // ==================== 베스트 리뷰 여부 ====================
    private Boolean isBestReview;

    // ==================== Inner Class: 답글 정보 ====================
    public static class ReplyInfo {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ReplyInfo() {}

        public ReplyInfo(Long id, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.content = content;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    // ==================== Constructors ====================
    public PublisherReviewResponse() {}

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Boolean getIsHidden() { return isHidden; }
    public void setIsHidden(Boolean isHidden) { this.isHidden = isHidden; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserProfileImage() { return userProfileImage; }
    public void setUserProfileImage(String userProfileImage) { this.userProfileImage = userProfileImage; }

    public Long getSeriesId() { return seriesId; }
    public void setSeriesId(Long seriesId) { this.seriesId = seriesId; }

    public String getSeriesTitle() { return seriesTitle; }
    public void setSeriesTitle(String seriesTitle) { this.seriesTitle = seriesTitle; }

    public Long getVolumeId() { return volumeId; }
    public void setVolumeId(Long volumeId) { this.volumeId = volumeId; }

    public String getVolumeTitle() { return volumeTitle; }
    public void setVolumeTitle(String volumeTitle) { this.volumeTitle = volumeTitle; }

    public String getVolumeCoverImage() { return volumeCoverImage; }
    public void setVolumeCoverImage(String volumeCoverImage) { this.volumeCoverImage = volumeCoverImage; }

    public ReplyInfo getReply() { return reply; }
    public void setReply(ReplyInfo reply) { this.reply = reply; }

    public Boolean getIsBestReview() { return isBestReview; }
    public void setIsBestReview(Boolean isBestReview) { this.isBestReview = isBestReview; }
}
