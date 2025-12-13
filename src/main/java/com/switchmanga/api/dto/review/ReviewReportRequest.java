package com.switchmanga.api.dto.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 신고 요청 DTO
 */
public class ReviewReportRequest {

    @NotNull(message = "신고 사유를 선택해주세요")
    private String reason;  // ABUSE, SPAM, SPOILER, FALSE, OTHER

    @Size(max = 500, message = "상세 사유는 500자 이내로 입력해주세요")
    private String description;

    // ==================== Constructors ====================
    public ReviewReportRequest() {}

    public ReviewReportRequest(String reason, String description) {
        this.reason = reason;
        this.description = description;
    }

    // ==================== Getters & Setters ====================
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}