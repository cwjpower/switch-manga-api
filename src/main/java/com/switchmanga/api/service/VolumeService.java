package com.switchmanga.api.service;

import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.repository.SeriesRepository;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolumeService {

    private final VolumeRepository volumeRepository;
    private final SeriesRepository seriesRepository;

    // 전체 Volume 조회
    public List<Volume> getAllVolumes() {
        return volumeRepository.findAll();
    }

    // Volume 상세 조회
    public Volume getVolumeById(Long id) {
        return volumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volume not found: " + id));
    }

    // 시리즈별 Volume 조회
    public List<Volume> getVolumesBySeries(Long seriesId) {
        return volumeRepository.findBySeriesId(seriesId);
    }

    // 시리즈별 Volume 조회 (권수 순서대로)
    public List<Volume> getVolumesBySeriesOrdered(Long seriesId) {
        return volumeRepository.findBySeriesIdOrderByVolumeNumberAsc(seriesId);
    }

    // 특정 시리즈의 특정 권 조회
    public Volume getVolumeBySeriesAndNumber(Long seriesId, Integer volumeNumber) {
        return volumeRepository.findBySeriesIdAndVolumeNumber(seriesId, volumeNumber)
                .orElseThrow(() -> new RuntimeException("Volume not found"));
    }

    // 활성화된 Volume만 조회
    public List<Volume> getActiveVolumes() {
        return volumeRepository.findByActiveTrue();
    }

    // 시리즈별 활성 Volume 조회
    public List<Volume> getActiveVolumesBySeries(Long seriesId) {
        return volumeRepository.findBySeriesIdAndActiveTrue(seriesId);
    }

    // 제목으로 검색
    public List<Volume> searchVolumesByTitle(String title) {
        return volumeRepository.findByTitleContainingIgnoreCase(title);
    }

    // ISBN으로 조회
    public Volume getVolumeByIsbn(String isbn) {
        return volumeRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Volume not found with ISBN: " + isbn));
    }

    // 시리즈별 Volume 개수
    public Long getVolumeCountBySeries(Long seriesId) {
        return volumeRepository.countBySeriesId(seriesId);
    }

    // Volume 생성
    @Transactional
    public Volume createVolume(Volume volume, Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new RuntimeException("Series not found: " + seriesId));
        volume.setSeries(series);
        return volumeRepository.save(volume);
    }

    // Volume 수정
    @Transactional
    public Volume updateVolume(Long id, Volume volume) {
        Volume existing = getVolumeById(id);
        // 필드 업데이트
        existing.setTitle(volume.getTitle());
        existing.setTitleEn(volume.getTitleEn());
        existing.setTitleJp(volume.getTitleJp());
        existing.setDescription(volume.getDescription());
        existing.setPrice(volume.getPrice());
        // ... 필요한 필드들 업데이트
        return volumeRepository.save(existing);
    }

    // 시리즈 변경
    @Transactional
    public Volume changeSeries(Long id, Long seriesId) {
        Volume volume = getVolumeById(id);
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new RuntimeException("Series not found: " + seriesId));
        volume.setSeries(series);
        return volumeRepository.save(volume);
    }

    // Volume 삭제 (Soft Delete)
    @Transactional
    public void deleteVolume(Long id) {
        Volume volume = getVolumeById(id);
        volume.setActive(false);
        volumeRepository.save(volume);
    }
}