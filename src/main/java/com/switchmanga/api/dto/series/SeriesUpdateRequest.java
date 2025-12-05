package com.switchmanga.api.dto.series;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesUpdateRequest {

    @Size(max = 200, message = "제목은 200자 이내여야 합니다")
    private String title;

    @Size(max = 200, message = "영문 제목은 200자 이내여야 합니다")
    private String titleEn;

    @Size(max = 200, message = "일본어 제목은 200자 이내여야 합니다")
    private String titleJp;

    @Size(max = 100, message = "작가명은 100자 이내여야 합니다")
    private String author;

    @Size(max = 255, message = "커버 이미지 URL은 255자 이내여야 합니다")
    private String coverImage;  // ✅ camelCase - getCoverImage() 자동 생성됨

    private String description;

    @Size(max = 20, message = "상태는 20자 이내여야 합니다")
    private String status;  // ✅ String 타입 - ONGOING, COMPLETED, CANCELLED

    private Long categoryId;
}