package com.switchmanga.api.service;

import com.switchmanga.api.dto.volume.*;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.switchmanga.api.repository.PublisherRepository;
import com.switchmanga.api.repository.SeriesRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Volume Service
 * - 기본 CRUD (Admin용)
 * - Publisher Portal용 메서드
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VolumeService {

    private final VolumeRepository volumeRepository;
    private final SeriesRepository seriesRepository;

    // ========================================
    // 📋 기본 CRUD (Admin용)
    // ========================================

    public List<Volume> getAllVolumes() {
        return volumeRepository.findAll();
    }

    public Volume getVolumeById(Long id) {
        return volumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ID: " + id));
    }

    public List<Volume> getVolumesBySeries(Long seriesId) {
        return volumeRepository.findBySeriesIdOrderByVolumeNumberAsc(seriesId);
    }

    public List<Volume> getVolumesBySeriesOrdered(Long seriesId) {
        return volumeRepository.findBySeriesIdOrderByVolumeNumberAsc(seriesId);
    }

    public Volume getVolumeBySeriesAndNumber(Long seriesId, Integer volumeNumber) {
        return volumeRepository.findBySeriesIdAndVolumeNumber(seriesId, volumeNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Volume을 찾을 수 없습니다. Series ID: " + seriesId + ", Volume Number: " + volumeNumber));
    }

    public List<Volume> getActiveVolumes() {
        return volumeRepository.findByActive(true);
    }

    public List<Volume> getActiveVolumesBySeries(Long seriesId) {
        return volumeRepository.findBySeriesIdAndActiveOrderByVolumeNumberAsc(seriesId, true);
    }

    public List<Volume> searchVolumesByTitle(String title) {
        return volumeRepository.findByTitleContainingIgnoreCase(title);
    }

    public Volume getVolumeByIsbn(String isbn) {
        return volumeRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ISBN: " + isbn));
    }

    public Long getVolumeCountBySeries(Long seriesId) {
        return volumeRepository.countBySeriesId(seriesId);
    }

    @Transactional
    public Volume createVolume(Volume volume, Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다. ID: " + seriesId));

        volumeRepository.findBySeriesIdAndVolumeNumber(seriesId, volume.getVolumeNumber())
                .ifPresent(v -> {
                    throw new RuntimeException(
                            "이미 존재하는 권수입니다. Series: " + seriesId + ", Volume: " + volume.getVolumeNumber());
                });

        volume.setSeries(series);
        return volumeRepository.save(volume);
    }

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

    @Transactional
    public Volume changeSeries(Long volumeId, Long newSeriesId) {
        Volume volume = getVolumeById(volumeId);
        Series newSeries = seriesRepository.findById(newSeriesId)
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다. ID: " + newSeriesId));
        volume.setSeries(newSeries);
        return volumeRepository.save(volume);
    }

    @Transactional
    public void deleteVolume(Long id) {
        Volume volume = getVolumeById(id);
        volumeRepository.delete(volume);
    }

    // ========================================
    // 🔒 PUBLISHER PORTAL 전용 메서드
    // ========================================

    /**
     * 내 Volume 목록 조회 (Portal용)
     */
    public List<VolumeListResponse> getMyVolumes(User user) {
        Publisher publisher = getPublisherFromUser(user);

        return volumeRepository.findByPublisherId(publisher.getId()).stream()
                .filter(v -> Boolean.TRUE.equals(v.getActive()))
                .map(VolumeListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 내 Volume 생성 (Portal용)
     */
    @Transactional
    public VolumeDetailResponse createMyVolume(User user, VolumeCreateRequest request) {
        Publisher publisher = getPublisherFromUser(user);

        // Series가 내 것인지 확인
        Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다: " + request.getSeriesId()));

        validateSeriesOwnership(publisher, series);

        // 같은 권수 중복 확인
        volumeRepository.findBySeriesIdAndVolumeNumber(request.getSeriesId(), request.getVolumeNumber())
                .ifPresent(v -> {
                    throw new RuntimeException("이미 존재하는 권수입니다: " + request.getVolumeNumber());
                });

        // Volume 생성
        Volume volume = new Volume();
        volume.setSeries(series);
        volume.setVolumeNumber(request.getVolumeNumber());
        volume.setTitle(request.getTitle());
        volume.setTitleEn(request.getTitleEn());
        volume.setTitleJp(request.getTitleJp());
        volume.setDescription(request.getDescription());
        volume.setPrice(request.getPrice());
        volume.setIsbn(request.getIsbn());
        volume.setPublicationDate(request.getPublicationDate());
        volume.setCoverImage(request.getCoverImage());
        volume.setActive(true);

        Volume saved = volumeRepository.save(volume);
        return VolumeDetailResponse.from(saved);
    }

    /**
     * 내 Volume 상세 조회 (Portal용)
     */
    public VolumeDetailResponse getMyVolumeDetail(User user, Long volumeId) {
        Publisher publisher = getPublisherFromUser(user);
        Volume volume = getVolumeById(volumeId);
        validateVolumeOwnership(publisher, volume);

        return VolumeDetailResponse.from(volume);
    }

    /**
     * 내 Volume 수정 (Portal용)
     */
    @Transactional
    public VolumeDetailResponse updateMyVolume(User user, Long volumeId, VolumeUpdateRequest request) {
        Publisher publisher = getPublisherFromUser(user);
        Volume volume = getVolumeById(volumeId);
        validateVolumeOwnership(publisher, volume);

        updateVolumeFromRequest(volume, request);

        return VolumeDetailResponse.from(volume);
    }

    /**
     * 내 Volume 삭제 (Portal용 - Soft Delete)
     */
    @Transactional
    public void deleteMyVolume(User user, Long volumeId) {
        Publisher publisher = getPublisherFromUser(user);
        Volume volume = getVolumeById(volumeId);
        validateVolumeOwnership(publisher, volume);

        volume.setActive(false);
        volumeRepository.save(volume);
    }

    // ========================================
    // 🔹 Portal용 유틸 메서드
    // ========================================

    /**
     * User에서 Publisher 가져오기
     */
    private Publisher getPublisherFromUser(User user) {
        Publisher publisher = user.getPublisher();
        if (publisher == null) {
            throw new RuntimeException("연결된 출판사가 없습니다.");
        }
        return publisher;
    }

    /**
     * Series 소유권 검증
     */
    private void validateSeriesOwnership(Publisher publisher, Series series) {
        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new RuntimeException("해당 시리즈에 접근할 권한이 없습니다.");
        }
    }

    /**
     * Volume 소유권 검증
     */
    private void validateVolumeOwnership(Publisher publisher, Volume volume) {
        if (!volume.getSeries().getPublisher().getId().equals(publisher.getId())) {
            throw new RuntimeException("해당 Volume에 접근할 권한이 없습니다.");
        }
    }

    /**
     * Request로 Volume 업데이트
     */
    private void updateVolumeFromRequest(Volume volume, VolumeUpdateRequest request) {
        if (request.getVolumeNumber() != null) {
            volume.setVolumeNumber(request.getVolumeNumber());
        }
        if (request.getTitle() != null) {
            volume.setTitle(request.getTitle());
        }
        if (request.getTitleEn() != null) {
            volume.setTitleEn(request.getTitleEn());
        }
        if (request.getTitleJp() != null) {
            volume.setTitleJp(request.getTitleJp());
        }
        if (request.getDescription() != null) {
            volume.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            volume.setPrice(request.getPrice());
        }
        if (request.getIsbn() != null) {
            volume.setIsbn(request.getIsbn());
        }
        if (request.getPublicationDate() != null) {
            volume.setPublicationDate(request.getPublicationDate());
        }
        if (request.getCoverImage() != null) {
            volume.setCoverImage(request.getCoverImage());
        }
        if (request.getActive() != null) {
            volume.setActive(request.getActive());
        }
    }
}