package com.switchmanga.api.dto.publisher;

import com.switchmanga.api.entity.Publisher;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 출판사 정보 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherInfoResponse {

    private Long id;
    private String name;
    private String nameEn;
    private String nameJp;
    private String logo;
    private String country;
    private String email;
    private String phone;
    private String website;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity → DTO 변환
    public static PublisherInfoResponse from(Publisher publisher) {
        return PublisherInfoResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .nameEn(publisher.getNameEn())
                .nameJp(publisher.getNameJp())
                .logo(publisher.getLogo())
                .country(publisher.getCountry())
                .email(publisher.getEmail())
                .phone(publisher.getPhone())
                .website(publisher.getWebsite())
                .description(publisher.getDescription())
                .active(publisher.getActive())
                .createdAt(publisher.getCreatedAt())
                .updatedAt(publisher.getUpdatedAt())
                .build();
    }
}