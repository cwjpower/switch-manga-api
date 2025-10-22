package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    // 국가별 출판사 조회
    List<Publisher> findByCountry(String country);

    // 활성화된 출판사 조회
    List<Publisher> findByActiveTrue();

    // 이름으로 검색 (부분 일치)
    List<Publisher> findByNameContaining(String name);
}