package com.switchmanga.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    @JsonIgnoreProperties({"seriesList", "hibernateLazyInitializer"})
    private Publisher publisher;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "title_en", length = 200)
    private String titleEn;

    @Column(name = "title_jp", length = 200)
    private String titleJp;

    @Column(length = 100)
    private String author;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default  // ✅ 추가
    @Column(length = 20)
    private String status = "ONGOING"; // ONGOING, COMPLETED, CANCELLED

    @Builder.Default  // ✅ 추가
    @Column(name = "total_volumes")
    private Integer totalVolumes = 0;

    @Builder.Default  // ✅ 추가
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Builder.Default  // ✅ 추가
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Builder.Default  // ✅ 추가
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"series", "hibernateLazyInitializer"})
    @Builder.Default
    private List<Volume> volumes = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}