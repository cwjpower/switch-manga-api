package com.switchmanga.api.dto.series;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 시리즈 생성 요청 DTO
 * POST /api/v1/publishers/me/series
 */
@Getter
@Setter
public class SeriesCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자 이내로 입력해주세요")
    private String title;
    
    @Size(max = 255, message = "영문 제목은 255자 이내로 입력해주세요")
    private String titleEn;
    
    @Size(max = 255, message = "일본어 제목은 255자 이내로 입력해주세요")
    private String titleJp;
    
    @Size(max = 2000, message = "설명은 2000자 이내로 입력해주세요")
    private String description;
    
    @NotBlank(message = "작가는 필수입니다")
    @Size(max = 255, message = "작가명은 255자 이내로 입력해주세요")
    private String author;
    
    @Size(max = 255, message = "아티스트명은 255자 이내로 입력해주세요")
    private String artist;
    
    @Size(max = 50, message = "상태는 50자 이내로 입력해주세요")
    private String status;
    
    @Size(max = 255, message = "이미지 URL은 255자 이내로 입력해주세요")
    private String coverImage;
}
