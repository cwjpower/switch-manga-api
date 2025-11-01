package com.switchmanga.api.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeUploadRequest {

    // 기본 정보
    private String title;           // 제목
    private String titleEn;         // 영문 제목
    private String titleJp;         // 일본어 제목

    private String author;          // 저자
    private String authorEn;        // 영문 저자
    private String authorJp;        // 일본어 저자

    // 가격 정보
    private Double price;       // 정가
    private Integer discountRate;   // 할인율 (0-100)

    // 시리즈 정보
    private Long seriesId;          // 시리즈 ID
    private Integer volumeNumber;   // 권수

    // 출판 정보
    private String isbn;            // ISBN
    private String publishedDate;   // 출판일 (YYYY-MM-DD)

    // 설명
    private String description;     // 설명
    private String descriptionEn;   // 영문 설명
    private String descriptionJp;   // 일본어 설명

    // 추가 설정
    private Integer previewPages;   // 미리보기 페이지 수
    private String ageRating;       // 연령 등급 (전체이용가, 15세, 19세)
    private Integer freeTrialDays;  // 무료 체험 기간 (일)

    // 상태
    private Boolean isFree;         // 무료 여부
    private Boolean hasAction;      // Action Viewer 지원 여부
}
