package com.switchmanga.api.dto.volume;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 권(Volume) 수정 요청 DTO
 * PUT /api/v1/publishers/me/volumes/{id}
 */
@Getter
@Setter
public class VolumeUpdateRequest {

    @Min(value = 1, message = "권 번호는 1 이상이어야 합니다")
    private Integer volumeNumber;
    
    @Size(max = 255, message = "제목은 255자 이내로 입력해주세요")
    private String title;
    
    @Size(max = 255, message = "영문 제목은 255자 이내로 입력해주세요")
    private String titleEn;
    
    @Size(max = 255, message = "일본어 제목은 255자 이내로 입력해주세요")
    private String titleJp;
    
    @Size(max = 2000, message = "설명은 2000자 이내로 입력해주세요")
    private String description;
    
    @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
    private Double price;
    
    @Size(max = 20, message = "ISBN은 20자 이내로 입력해주세요")
    private String isbn;
    
    private LocalDateTime publicationDate;
    
    @Size(max = 255, message = "이미지 URL은 255자 이내로 입력해주세요")
    private String coverImage;
    
    private Boolean active;
}
