package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    // 권별 페이지 목록 조회
    List<Page> findByVolumeId(Long volumeId);

    // 권별 페이지 목록 조회 (페이지 번호 순서대로)
    List<Page> findByVolumeIdOrderByPageNumberAsc(Long volumeId);

    // 권별 + 페이지 번호로 조회
    Optional<Page> findByVolumeIdAndPageNumber(Long volumeId, Integer pageNumber);

    // 권별 페이지 개수
    Long countByVolumeId(Long volumeId);

    // 권별 페이지 전체 삭제
    void deleteByVolumeId(Long volumeId);

    // 특정 페이지 번호 이상의 페이지들 조회
    List<Page> findByVolumeIdAndPageNumberGreaterThanEqual(Long volumeId, Integer pageNumber);
}
