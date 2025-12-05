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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VolumeService {

    private final VolumeRepository volumeRepository;
    private final SeriesRepository seriesRepository;

    // ========================================
    // Controller에서 호출하는 메서드들 (14개)
    // ========================================

    /**
     * 전체 볼륨 목록 조회
     */
    public List<Volume> getAllVolumes() {
        return volumeRepository.findAll();
    }

    /**
     * 볼륨 ID로 조회
     */
    public Volume getVolumeById(Long id) {
        return volumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + id));
    }

    /**
     * 시리즈별 볼륨 목록 조회
     */
    public List<Volume> getVolumesBySeries(Long seriesId) {
        return volumeRepository.findBySeriesId(seriesId);
    }

    /**
     * 시리즈별 볼륨 목록 조회 (권 번호 순)
     */
    public List<Volume> getVolumesBySeriesOrdered(Long seriesId) {
        return volumeRepository.findBySeriesIdOrderByVolumeNumberAsc(seriesId);
    }

    /**
     * 시리즈 ID + 권 번호로 조회
     */
    public Volume getVolumeBySeriesAndNumber(Long seriesId, Integer volumeNumber) {
        return volumeRepository.findBySeriesIdAndVolumeNumber(seriesId, volumeNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "볼륨을 찾을 수 없습니다. seriesId: " + seriesId + ", volumeNumber: " + volumeNumber));
    }

    /**
     * 활성 볼륨 목록 조회 (isFree가 아닌 것 또는 전체)
     */
    public List<Volume> getActiveVolumes() {
        // 모든 볼륨 반환 (active 필드가 없으므로)
        return volumeRepository.findAll();
    }

    /**
     * 시리즈별 활성 볼륨 목록 조회
     */
    public List<Volume> getActiveVolumesBySeries(Long seriesId) {
        return volumeRepository.findBySeriesId(seriesId);
    }

    /**
     * 제목으로 볼륨 검색
     */
    public List<Volume> searchVolumesByTitle(String title) {
        return volumeRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * ISBN으로 볼륨 조회
     */
    public Volume getVolumeByIsbn(String isbn) {
        return volumeRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("ISBN에 해당하는 볼륨을 찾을 수 없습니다: " + isbn));
    }

    /**
     * 시리즈별 볼륨 개수 조회
     */
    public Long getVolumeCountBySeries(Long seriesId) {
        return volumeRepository.countBySeriesId(seriesId);
    }

    /**
     * 볼륨 생성
     */
    @Transactional
    public Volume createVolume(Volume volume, Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        volume.setSeries(series);
        Volume saved = volumeRepository.save(volume);

        // 시리즈 totalVolumes 업데이트
        updateSeriesTotalVolumes(seriesId);

        return saved;
    }

    /**
     * 볼륨 수정
     */
    @Transactional
    public Volume updateVolume(Long id, Volume volumeDetails) {
        Volume volume = getVolumeById(id);

        if (volumeDetails.getVolumeNumber() != null) {
            volume.setVolumeNumber(volumeDetails.getVolumeNumber());
        }
        if (volumeDetails.getTitle() != null) {
            volume.setTitle(volumeDetails.getTitle());
        }
        if (volumeDetails.getTitleEn() != null) {
            volume.setTitleEn(volumeDetails.getTitleEn());
        }
        if (volumeDetails.getTitleJp() != null) {
            volume.setTitleJp(volumeDetails.getTitleJp());
        }
        if (volumeDetails.getCoverImage() != null) {
            volume.setCoverImage(volumeDetails.getCoverImage());
        }
        if (volumeDetails.getDescription() != null) {
            volume.setDescription(volumeDetails.getDescription());
        }
        if (volumeDetails.getPrice() != null) {
            volume.setPrice(volumeDetails.getPrice());
        }
        if (volumeDetails.getDiscountRate() != null) {
            volume.setDiscountRate(volumeDetails.getDiscountRate());
        }
        if (volumeDetails.getTotalPages() != null) {
            volume.setTotalPages(volumeDetails.getTotalPages());
        }
        if (volumeDetails.getPublishedDate() != null) {
            volume.setPublishedDate(volumeDetails.getPublishedDate());
        }
        if (volumeDetails.getIsFree() != null) {
            volume.setIsFree(volumeDetails.getIsFree());
        }

        return volumeRepository.save(volume);
    }

    /**
     * 볼륨 시리즈 변경
     */
    @Transactional
    public Volume changeSeries(Long volumeId, Long seriesId) {
        Volume volume = getVolumeById(volumeId);
        Long oldSeriesId = volume.getSeries().getId();

        Series newSeries = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        volume.setSeries(newSeries);
        Volume saved = volumeRepository.save(volume);

        // 양쪽 시리즈의 totalVolumes 업데이트
        updateSeriesTotalVolumes(oldSeriesId);
        updateSeriesTotalVolumes(seriesId);

        return saved;
    }

    /**
     * 볼륨 삭제
     */
    @Transactional
    public void deleteVolume(Long id) {
        Volume volume = getVolumeById(id);
        Long seriesId = volume.getSeries().getId();

        volumeRepository.delete(volume);

        // 시리즈 totalVolumes 업데이트
        updateSeriesTotalVolumes(seriesId);
    }

    // ========================================
    // Helper 메서드
    // ========================================

    /**
     * 시리즈의 totalVolumes 업데이트
     */
    private void updateSeriesTotalVolumes(Long seriesId) {
        Series series = seriesRepository.findById(seriesId).orElse(null);
        if (series != null) {
            long count = volumeRepository.countBySeriesId(seriesId);
            series.setTotalVolumes((int) count);
            seriesRepository.save(series);
        }
    }
}