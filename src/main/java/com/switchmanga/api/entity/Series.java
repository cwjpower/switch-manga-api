package com.switchmanga.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "title_en", length = 255)
    private String titleEn;

    @Column(name = "title_jp", length = 255)
    private String titleJp;

    @Column(length = 255)
    private String author;

    @Column(length = 255)
    private String artist;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String status;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Publisher publisher;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}