package com.switchmanga.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @JsonIgnoreProperties({"volumes", "hibernateLazyInitializer"})
    private Series series;

    @Column(name = "volume_number", nullable = false)
    private Integer volumeNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "title_en", length = 200)
    private String titleEn;

    @Column(name = "title_jp", length = 200)
    private String titleJp;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default  // ✅ 추가
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Builder.Default  // ✅ 추가
    @Column(name = "discount_rate")
    private Integer discountRate = 0;

    @Builder.Default  // ✅ 추가
    @Column(name = "total_pages")
    private Integer totalPages = 0;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(length = 20)
    private String isbn;

    @Builder.Default  // ✅ 추가
    @Column(name = "is_free")
    private Boolean isFree = false;

    @Builder.Default  // ✅ 추가
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Builder.Default  // ✅ 추가
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Builder.Default  // ✅ 추가
    @Column(name = "purchase_count")
    private Integer purchaseCount = 0;

    @Builder.Default  // ✅ 추가
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Builder.Default  // ✅ active 필드 추가
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default  // ✅ status 필드 추가
    @Column(length = 20)
    private String status = "DRAFT";

    @Builder.Default  // ✅ freePages 필드 추가
    @Column(name = "free_pages")
    private Integer freePages = 0;

    @Column(name = "zip_file", length = 255)  // ✅ ZIP 파일명
    private String zipFile;

    @Column(name = "zip_file_path", length = 500)  // ✅ ZIP 파일 경로
    private String zipFilePath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}