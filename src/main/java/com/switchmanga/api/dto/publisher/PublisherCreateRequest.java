package com.switchmanga.api.dto.publisher;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;

/**
 * Publisher 생성 요청 DTO (ADMIN 전용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherCreateRequest {
    
    @NotBlank(message = "출판사명은 필수입니다")
    @Size(max = 100, message = "출판사명은 100자 이하여야 합니다")
    private String name;
    
    @Size(max = 100, message = "영문 출판사명은 100자 이하여야 합니다")
    private String nameEn;
    
    @Size(max = 100, message = "일본어 출판사명은 100자 이하여야 합니다")
    private String nameJp;
    
    @NotBlank(message = "국가는 필수입니다")
    @Size(max = 50, message = "국가 코드는 50자 이하여야 합니다")
    private String country;  // US, JP, KR 등
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;
    
    @Pattern(regexp = "^[0-9-+() ]*$", message = "올바른 전화번호 형식이 아닙니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;
    
    @Size(max = 255, message = "웹사이트 URL은 255자 이하여야 합니다")
    private String website;
    
    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
    private String description;
}
