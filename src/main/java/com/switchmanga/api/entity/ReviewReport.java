package com.switchmanga.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_reports")
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "reporter_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ReporterType reporterType;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ==================== Enums ====================

    public enum ReporterType {
        PUBLISHER, USER
    }

    public enum ReportReason {
        ABUSE,      // 욕설/비방
        SPAM,       // 스팸/광고
        SPOILER,    // 스포일러
        FALSE,      // 허위 사실
        OTHER       // 기타
    }

    // ==================== Constructors ====================

    public ReviewReport() {
    }

    public ReviewReport(Review review, Long reporterId, ReporterType reporterType, 
                        ReportReason reason, String description) {
        this.review = review;
        this.reporterId = reporterId;
        this.reporterType = reporterType;
        this.reason = reason;
        this.description = description;
    }

    // ==================== Lifecycle ====================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ==================== Getters & Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public ReporterType getReporterType() {
        return reporterType;
    }

    public void setReporterType(ReporterType reporterType) {
        this.reporterType = reporterType;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
