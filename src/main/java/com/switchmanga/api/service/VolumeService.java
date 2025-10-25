package com.switchmanga.api.service;

import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.repository.VolumeRepository;
import com.switchmanga.api.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ID: " + id));
    }

    // 시리즈별 Volume 조회
    public List<Volume> getVolumesBySeries(Long seriesId) {
        // findBySeriesId() → findBySeriesIdOrderByVolumeNumberAsc()로 수정
        return volumeRepository.findBySeriesIdOrderByVolumeNumberAsc(seriesId);
    }

    // 시리즈별 Volume 조회 (권수 순서대로)
    public List<Volume> getVolumesBySeriesOrdered(Long seriesId) {
        return volumeRepository.findBySeriesIdOrderByVolumeNumberAsc(seriesId);
    }

    // 특정 시리즈의 특정 권 조회
    public Volume getVolumeBySeriesAndNumber(Long seriesId, Integer volumeNumber) {
        return volumeRepository.findBySeriesIdAndVolumeNumber(seriesId, volumeNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Volume을 찾을 수 없습니다. Series ID: " + seriesId + ", Volume Number: " + volumeNumber));
    }

    // 활성화된 Volume만 조회
    public List<Volume> getActiveVolumes() {
        // findByActiveTrue() → findByActive(true)로 수정
        return volumeRepository.findByActive(true);
    }

    // 시리즈별 활성 Volume 조회
    public List<Volume> getActiveVolumesBySeries(Long seriesId) {
        // findBySeriesIdAndActiveTrue() → findBySeriesIdAndActiveOrderByVolumeNumberAsc()로 수정
        return volumeRepository.findBySeriesIdAndActiveOrderByVolumeNumberAsc(seriesId, true);
    }

    // 제목으로 검색
    public List<Volume> searchVolumesByTitle(String title) {
        // findByTitleContaining() → findByTitleContainingIgnoreCase()로 수정
        return volumeRepository.findByTitleContainingIgnoreCase(title);
    }

    // ISBN으로 조회
    public Volume getVolumeByIsbn(String isbn) {
        return volumeRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ISBN: " + isbn));
    }

    // 시리즈별 Volume 개수
    public Long getVolumeCountBySeries(Long seriesId) {
        return volumeRepository.countBySeriesId(seriesId);
    }

    // Volume 생성
    @Transactional
    public Volume createVolume(Volume volume, Long seriesId) {
        // 시리즈 존재 확인
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다. ID: " + seriesId));

        // 같은 시리즈에 같은 권수가 이미 있는지 확인
        volumeRepository.findBySeriesIdAndVolumeNumber(seriesId, volume.getVolumeNumber())
                .ifPresent(v -> {
                    throw new RuntimeException(
                            "이미 존재하는 권수입니다. Series: " + seriesId + ", Volume: " + volume.getVolumeNumber());
                });

        // Volume에 시리즈 설정
        volume.setSeries(series);

        return volumeRepository.save(volume);
    }

    // Volume 수정
    @Transactional
    public Volume updateVolume(Long id, Volume volumeDetails) {
        Volume volume = getVolumeById(id);

        volume.setVolumeNumber(volumeDetails.getVolumeNumber());
        volume.setTitle(volumeDetails.getTitle());
        volume.setTitleEn(volumeDetails.getTitleEn());
        volume.setTitleJp(volumeDetails.getTitleJp());
        volume.setCoverImage(volumeDetails.getCoverImage());
        volume.setDescription(volumeDetails.getDescription());
        volume.setPageCount(volumeDetails.getPageCount());
        volume.setIsbn(volumeDetails.getIsbn());
        volume.setPublicationDate(volumeDetails.getPublicationDate());
        volume.setPrice(volumeDetails.getPrice());
        volume.setActive(volumeDetails.getActive());

        return volumeRepository.save(volume);
    }

    // 시리즈 변경
    @Transactional
    public Volume changeSeries(Long volumeId, Long newSeriesId) {
        Volume volume = getVolumeById(volumeId);
        Series newSeries = seriesRepository.findById(newSeriesId)
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다. ID: " + newSeriesId));

        volume.setSeries(newSeries);

        return volumeRepository.save(volume);
    }

    // Volume 삭제
    @Transactional
    public void deleteVolume(Long id) {
        Volume volume = getVolumeById(id);
        volumeRepository.delete(volume);
    }
}
