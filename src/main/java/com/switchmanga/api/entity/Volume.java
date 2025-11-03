package com.switchmanga.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "title_en", length = 200)
    private String titleEn;

    @Column(name = "title_jp", length = 200)
    private String titleJp;

    @Column(name = "volume_number", nullable = false)
    private Integer volumeNumber;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_jp", columnDefinition = "TEXT")
    private String descriptionJp;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    // 페이지 관련
    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "preview_pages")
    private Integer previewPages;

    // 연령/등급
    @Column(name = "age_rating", length = 10)
    private String ageRating;

    // 무료 체험
    @Column(name = "free_trial_days")
    private Integer freeTrialDays;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_rate")
    @Builder.Default
    private Integer discountRate = 0;

    @Column(name = "isbn", length = 10)
    private String isbn;

    @Column(length = 100)
    private String author;

    @Column(name = "author_en", length = 100)
    private String authorEn;

    @Column(name = "author_jp", length = 100)
    private String authorJp;

    @Column(length = 100)
    private String artist;

    @Column(name = "artist_en", length = 100)
    private String artistEn;

    @Column(name = "artist_jp", length = 100)
    private String artistJp;

    @Column(name = "has_action")
    @Builder.Default
    private Boolean hasAction = false;

    @Column(name = "avf_file_path", length = 500)
    private String avfFilePath;

    @Column(name = "pages_directory", length = 500)
    private String pagesDirectory;

    @Column(name = "is_free")
    @Builder.Default
    private Boolean isFree = false;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "average_rating", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 관계 설정
    @JsonIgnoreProperties({"volumes", "publisher"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    @OneToMany(mappedBy = "volume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Page> pages = new ArrayList<>();

    @OneToMany(mappedBy = "volume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "volume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}