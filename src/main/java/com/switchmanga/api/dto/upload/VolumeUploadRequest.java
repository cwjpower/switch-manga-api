package com.switchmanga.api.dto.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class VolumeUploadRequest {

    @NotNull(message = "시리즈 ID는 필수입니다")
    private Long seriesId;

    @NotNull(message = "권 번호는 필수입니다")
    @PositiveOrZero(message = "권 번호는 0 이상이어야 합니다")
    private Integer volumeNumber;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    private String titleEn;

    private String titleJp;

    private String author;

    private String authorEn;

    private String authorJp;

    private String description;

    private String descriptionEn;

    private String descriptionJp;

    private String coverImage;

    @PositiveOrZero(message = "가격은 0 이상이어야 합니다")
    private Double price;

    private Integer discountRate;

    private String publishedDate;  // String으로 받고 Service에서 변환

    private String isbn;

    private Integer totalPages;

    private Integer previewPages;

    private String ageRating;

    private Integer freeTrialDays;

    private Boolean isFree;

    private Boolean hasAction;

    private Long fileSize;

    // 추가 가능한 필드들
    private String status;

    private String language;

    private String genre;

    private String tags;
}