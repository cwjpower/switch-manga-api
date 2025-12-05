package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    // Volume ID로 조회
    List<Page> findByVolumeId(Long volumeId);

    List<Page> findByVolumeIdOrderByPageNumberAsc(Long volumeId);

    // 특정 페이지 번호 조회
    Optional<Page> findByVolumeIdAndPageNumber(Long volumeId, Integer pageNumber);

    // 개수 조회
    Long countByVolumeId(Long volumeId);

    // 삭제
    void deleteByVolumeId(Long volumeId);

    // 존재 여부 확인
    boolean existsByVolumeIdAndPageNumber(Long volumeId, Integer pageNumber);
}