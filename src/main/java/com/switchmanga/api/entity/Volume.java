package com.switchmanga.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "volumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Series series;

    @Column(name = "volume_number", nullable = false)
    private Integer volumeNumber;

    // 제목 (다국어)
    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "title_en", length = 255)
    private String titleEn;

    @Column(name = "title_jp", length = 255)
    private String titleJp;

    // 저자 (다국어)
    @Column(length = 255)
    private String author;

    @Column(name = "author_en", length = 255)
    private String authorEn;

    @Column(name = "author_jp", length = 255)
    private String authorJp;

    // 설명 (다국어)
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_jp", columnDefinition = "TEXT")
    private String descriptionJp;

    // 표지 이미지
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    // 페이지 정보
    @Column(name = "pages_directory", length = 500)
    private String pagesDirectory;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "preview_pages")
    private Integer previewPages;

    // 가격 정보
    @Column
    private Double price;

    @Column(name = "discount_rate", columnDefinition = "INT DEFAULT 0")
    private Integer discountRate = 0;

    // 출판 정보
// 출판 정보
    @Column(length = 20)
    private String isbn;

    @Column(name = "publication_date")
    private LocalDateTime publicationDate;  // LocalDate → LocalDateTime으로 변경

    @Column(name = "age_rating", length = 20)
    private String ageRating;

    // Action Viewer 정보
    @Column(name = "has_action", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean hasAction = false;

    @Column(name = "avf_file_path", length = 500)
    private String avfFilePath;

    // 무료/체험 정보
    @Column(name = "is_free", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isFree = false;

    @Column(name = "free_trial_days")
    private Integer freeTrialDays;

    // 통계 정보
    @Column(name = "view_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;

    @Column(name = "average_rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0.0")
    private Double averageRating = 0.0;

    // 상태
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active = true;

    // 타임스탬프
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}