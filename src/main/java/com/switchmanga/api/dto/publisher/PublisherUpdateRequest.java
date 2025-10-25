package com.switchmanga.api.dto.publisher;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * 출판사 정보 수정 요청 DTO
 */
@Getter
@Setter
public class PublisherUpdateRequest {

    private String name;
    private String nameEn;
    private String nameJp;
    private String logo;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    private String phone;
    private String website;
    private String description;
}