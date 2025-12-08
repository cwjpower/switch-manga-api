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

    @Builder.Default
    @Column(name = "default_price", precision = 10, scale = 2)
    private BigDecimal defaultPrice = new BigDecimal("3000.00");

    @Builder.Default
    @Column(name = "pricing_model", length = 20)
    private String pricingModel = "FIXED";

    @Column(name = "bundle_price", precision = 10, scale = 2)
    private BigDecimal bundlePrice;

    @Builder.Default
    @Column(name = "bundle_discount_rate")
    private Integer bundleDiscountRate = 0;

    @Column(name = "rental_price", precision = 10, scale = 2)
    private BigDecimal rentalPrice;

    @Builder.Default
    @Column(name = "rental_days")
    private Integer rentalDays = 7;

    @Column(name = "subscription_price", precision = 10, scale = 2)
    private BigDecimal subscriptionPrice;

    @Builder.Default
    @Column(name = "subscription_period", length = 20)
    private String subscriptionPeriod = "MONTHLY";

    @Builder.Default
    @Column(name = "free_volumes")
    private Integer freeVolumes = 0;

    @Builder.Default
    @Column(length = 20)
    private String status = "ONGOING";

    @Builder.Default
    @Column(name = "total_volumes")
    private Integer totalVolumes = 0;

    @Builder.Default
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Builder.Default
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "artist", length = 100)
    private String artist;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Builder.Default
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

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

    public Long getPublisherId() {
        return publisher != null ? publisher.getId() : null;
    }

    public void setPublisherId(Long publisherId) {
        if (this.publisher == null) {
            this.publisher = new Publisher();
        }
        this.publisher.setId(publisherId);
    }
}