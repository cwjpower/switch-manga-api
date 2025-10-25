package com.switchmanga.api.dto.volume;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 권(Volume) 생성 요청 DTO
 * POST /api/v1/publishers/me/volumes
 */
@Getter
@Setter
public class VolumeCreateRequest {

    @NotNull(message = "시리즈 ID는 필수입니다")
    private Long seriesId;
    
    @NotNull(message = "권 번호는 필수입니다")
    @Min(value = 1, message = "권 번호는 1 이상이어야 합니다")
    private Integer volumeNumber;
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자 이내로 입력해주세요")
    private String title;
    
    @Size(max = 255, message = "영문 제목은 255자 이내로 입력해주세요")
    private String titleEn;
    
    @Size(max = 255, message = "일본어 제목은 255자 이내로 입력해주세요")
    private String titleJp;
    
    @Size(max = 2000, message = "설명은 2000자 이내로 입력해주세요")
    private String description;
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
    private Double price;
    
    @Size(max = 20, message = "ISBN은 20자 이내로 입력해주세요")
    private String isbn;
    
    private LocalDateTime publicationDate;
    
    @Size(max = 255, message = "이미지 URL은 255자 이내로 입력해주세요")
    private String coverImage;
}
