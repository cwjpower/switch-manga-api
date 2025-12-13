package com.switchmanga.api.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 답글 작성/수정 요청 DTO
 */
public class ReviewReplyRequest {

    @NotBlank(message = "답글 내용을 입력해주세요")
    @Size(min = 1, max = 500, message = "답글은 1~500자 사이로 입력해주세요")
    private String content;

    // ==================== Constructors ====================
    public ReviewReplyRequest() {}

    public ReviewReplyRequest(String content) {
        this.content = content;
    }

    // ==================== Getters & Setters ====================
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}