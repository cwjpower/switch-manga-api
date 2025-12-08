package com.switchmanga.api.dto.volume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeCreateRequest {

    @NotNull(message = "시리즈 ID는 필수입니다")
    private Long seriesId;

    @NotNull(message = "권 번호는 필수입니다")
    @PositiveOrZero(message = "권 번호는 0 이상이어야 합니다")
    private Integer volumeNumber;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이내여야 합니다")
    private String title;

    @Size(max = 200, message = "영문 제목은 200자 이내여야 합니다")
    private String titleEn;

    @Size(max = 200, message = "일본어 제목은 200자 이내여야 합니다")
    private String titleJp;

    @Size(max = 255, message = "커버 이미지 URL은 255자 이내여야 합니다")
    private String coverImage;

    private String description;

    @NotNull(message = "가격은 필수입니다")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다")
    private BigDecimal price;

    @PositiveOrZero(message = "할인율은 0 이상이어야 합니다")
    private Integer discountRate;

    @PositiveOrZero(message = "페이지 수는 0 이상이어야 합니다")
    private Integer totalPages;

    private LocalDate publishedDate;

    private Boolean isFree;

    @Size(max = 20, message = "상태는 20자 이내여야 합니다")
    private String status;

    @PositiveOrZero(message = "무료 페이지 수는 0 이상이어야 합니다")
    private Integer freePages;

    @Size(max = 255, message = "ZIP 파일명은 255자 이내여야 합니다")
    private String zipFile;

    @Size(max = 500, message = "ZIP 파일 경로는 500자 이내여야 합니다")
    private String zipFilePath;
}