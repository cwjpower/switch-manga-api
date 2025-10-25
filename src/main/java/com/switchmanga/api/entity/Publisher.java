package com.switchmanga.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publishers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ← 이거 추가!
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "name_jp", length = 100)
    private String nameJp;

    @Column(length = 255)
    private String logo;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String website;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 관계 추가
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Series> series = new ArrayList<>();
}
