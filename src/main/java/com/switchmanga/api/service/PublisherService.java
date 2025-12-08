package com.switchmanga.api.service;

import com.switchmanga.api.dto.publisher.*;
import com.switchmanga.api.dto.series.*;
import com.switchmanga.api.dto.volume.*;
import com.switchmanga.api.entity.*;
import com.switchmanga.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final SeriesRepository seriesRepository;
    private final VolumeRepository volumeRepository;
    private final UserRepository userRepository;

    // ========================================
    // Publisher 관련 메서드
    // ========================================

    /**
     * 내 출판사 정보 조회
     */
    public PublisherInfoResponse getMyInfo(User user) {
        Publisher publisher = getPublisherByUser(user);
        return PublisherInfoResponse.from(publisher);
    }

    /**
     * 출판사 정보 수정
     */
    @Transactional
    public PublisherInfoResponse updateMyInfo(User user, PublisherUpdateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        if (request.getName() != null) {
            publisher.setName(request.getName());
        }
        if (request.getNameEn() != null) {
            publisher.setNameEn(request.getNameEn());
        }
        if (request.getNameJp() != null) {
            publisher.setNameJp(request.getNameJp());
        }
        if (request.getLogo() != null) {
            publisher.setLogo(request.getLogo());
        }
        if (request.getEmail() != null) {
            publisher.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            publisher.setPhone(request.getPhone());
        }
        if (request.getWebsite() != null) {
            publisher.setWebsite(request.getWebsite());
        }
        if (request.getDescription() != null) {
            publisher.setDescription(request.getDescription());
        }

        Publisher saved = publisherRepository.save(publisher);
        return PublisherInfoResponse.from(saved);
    }

    /**
     * 출판사 통계 조회
     */
    public PublisherStatsResponse getMyStats(User user) {
        Publisher publisher = getPublisherByUser(user);
        Long publisherId = publisher.getId();

        long totalSeries = seriesRepository.countByPublisherId(publisherId);
        long totalVolumes = volumeRepository.countBySeriesPublisherId(publisherId);

        return PublisherStatsResponse.builder()
                .totalSeries(totalSeries)
                .totalVolumes(totalVolumes)
                .totalRevenue(0.0)  // ✅ Double 타입으로 수정
                .totalOrders(0L)
                .build();
    }

    // ========================================
    // Series 관련 메서드
    // ========================================

    /**
     * 내 시리즈 목록 조회
     */
    public Map<String, Object> getMySeries(User user, int page, int size, String status, String search) {
        Publisher publisher = getPublisherByUser(user);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Series> seriesPage;

        if (search != null && !search.isEmpty()) {
            seriesPage = seriesRepository.findByPublisherIdAndTitleContainingIgnoreCase(
                    publisher.getId(), search, pageable);
        } else if (status != null && !status.isEmpty()) {
            seriesPage = seriesRepository.findByPublisherIdAndStatus(
                    publisher.getId(), status, pageable);
        } else {
            seriesPage = seriesRepository.findByPublisherId(publisher.getId(), pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", seriesPage.getContent().stream()
                .map(series -> {
                    SeriesListResponse response = SeriesListResponse.from(series);
                    // ✅ Volume 개수 계산!
                    int volumeCount = volumeRepository.countBySeriesId(series.getId()).intValue();
                    response.setVolumeCount(volumeCount);
                    return response;
                })
                .collect(Collectors.toList()));
        result.put("page", seriesPage.getNumber());
        result.put("size", seriesPage.getSize());
        result.put("totalElements", seriesPage.getTotalElements());
        result.put("totalPages", seriesPage.getTotalPages());

        return result;
    }

    /**
     * 시리즈 상세 조회
     */
    public SeriesDetailResponse getMySeriesDetail(User user, Long seriesId) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        // 권한 확인
        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 시리즈에 대한 접근 권한이 없습니다.");
        }

        return SeriesDetailResponse.from(series);
    }

    /**
     * 시리즈 생성
     */
    @Transactional
    public Series createSeries(User user, SeriesCreateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        Series series = new Series();
        series.setPublisher(publisher);
        series.setTitle(request.getTitle());
        series.setTitleEn(request.getTitleEn());
        series.setTitleJp(request.getTitleJp());
        series.setAuthor(request.getAuthor());
        series.setDescription(request.getDescription());
        series.setCoverImage(request.getCoverImage());

        // status는 String 타입 그대로 사용
        if (request.getStatus() != null) {
            series.setStatus(request.getStatus());
        } else {
            series.setStatus("ONGOING");
        }

        // categoryId 설정 (있으면)
        if (request.getCategoryId() != null) {
            series.setCategoryId(request.getCategoryId());
        }

        return seriesRepository.save(series);
    }

    /**
     * 시리즈 수정
     */
    @Transactional
    public Series updateSeries(User user, Long seriesId, SeriesUpdateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        // 권한 확인
        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 시리즈에 대한 수정 권한이 없습니다.");
        }

        // 필드 업데이트
        if (request.getTitle() != null) {
            series.setTitle(request.getTitle());
        }
        if (request.getTitleEn() != null) {
            series.setTitleEn(request.getTitleEn());
        }
        if (request.getTitleJp() != null) {
            series.setTitleJp(request.getTitleJp());
        }
        if (request.getAuthor() != null) {
            series.setAuthor(request.getAuthor());
        }
        if (request.getDescription() != null) {
            series.setDescription(request.getDescription());
        }
        if (request.getCoverImage() != null) {
            series.setCoverImage(request.getCoverImage());
        }
        // status는 String 타입 그대로 사용
        if (request.getStatus() != null) {
            series.setStatus(request.getStatus());
        }
        if (request.getCategoryId() != null) {
            series.setCategoryId(request.getCategoryId());
        }

        return seriesRepository.save(series);
    }

    /**
     * 시리즈 삭제
     */
    @Transactional
    public void deleteSeries(User user, Long seriesId) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        // 권한 확인
        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 시리즈에 대한 삭제 권한이 없습니다.");
        }

        seriesRepository.delete(series);
    }

    // ========================================
    // Volume 관련 메서드
    // ========================================

    /**
     * 내 볼륨 목록 조회
     */
    public Map<String, Object> getMyVolumes(User user, int page, int size, Long seriesId) {
        Publisher publisher = getPublisherByUser(user);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Volume> volumePage;

        if (seriesId != null) {
            // 시리즈 권한 확인
            Series series = seriesRepository.findById(seriesId)
                    .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

            if (!series.getPublisher().getId().equals(publisher.getId())) {
                throw new AccessDeniedException("이 시리즈에 대한 접근 권한이 없습니다.");
            }

            volumePage = volumeRepository.findBySeriesId(seriesId, pageable);
        } else {
            volumePage = volumeRepository.findBySeriesPublisherId(publisher.getId(), pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", volumePage.getContent().stream()
                .map(VolumeListResponse::from)
                .collect(Collectors.toList()));
        result.put("page", volumePage.getNumber());
        result.put("size", volumePage.getSize());
        result.put("totalElements", volumePage.getTotalElements());
        result.put("totalPages", volumePage.getTotalPages());

        return result;
    }

    /**
     * 볼륨 상세 조회
     */
    public VolumeDetailResponse getMyVolumeDetail(User user, Long volumeId) {
        Publisher publisher = getPublisherByUser(user);

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        // 권한 확인
        if (!volume.getSeries().getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 볼륨에 대한 접근 권한이 없습니다.");
        }

        return VolumeDetailResponse.from(volume);
    }

    /**
     * 볼륨 생성
     */
    @Transactional
    public Volume createVolume(User user, VolumeCreateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + request.getSeriesId()));

        // 권한 확인
        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 시리즈에 볼륨을 추가할 권한이 없습니다.");
        }

        Volume volume = new Volume();
        volume.setSeries(series);
        volume.setVolumeNumber(request.getVolumeNumber());
        volume.setTitle(request.getTitle());
        volume.setTitleEn(request.getTitleEn());
        volume.setTitleJp(request.getTitleJp());
        volume.setCoverImage(request.getCoverImage());
        volume.setDescription(request.getDescription());
        volume.setPrice(request.getPrice());
        volume.setDiscountRate(request.getDiscountRate());
        volume.setTotalPages(request.getTotalPages());
        volume.setPublishedDate(request.getPublishedDate());
        volume.setIsFree(request.getIsFree() != null ? request.getIsFree() : false);
        volume.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        volume.setFreePages(request.getFreePages() != null ? request.getFreePages() : 0);
        volume.setZipFile(request.getZipFile());  // ✅ ZIP 파일명
        volume.setZipFilePath(request.getZipFilePath());  // ✅ ZIP 파일 경로

        Volume saved = volumeRepository.save(volume);

        // 시리즈의 totalVolumes 업데이트
        updateSeriesTotalVolumes(series.getId());

        return saved;
    }

    /**
     * 볼륨 수정
     */
    @Transactional
    public Volume updateVolume(User user, Long volumeId, VolumeUpdateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        // 권한 확인
        if (!volume.getSeries().getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 볼륨에 대한 수정 권한이 없습니다.");
        }

        // 필드 업데이트
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
        if (request.getCoverImage() != null) {
            volume.setCoverImage(request.getCoverImage());
        }
        if (request.getDescription() != null) {
            volume.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            volume.setPrice(request.getPrice());
        }
        if (request.getDiscountRate() != null) {
            volume.setDiscountRate(request.getDiscountRate());
        }
        if (request.getTotalPages() != null) {
            volume.setTotalPages(request.getTotalPages());
        }
        if (request.getPublishedDate() != null) {
            volume.setPublishedDate(request.getPublishedDate());
        }
        if (request.getIsFree() != null) {
            volume.setIsFree(request.getIsFree());
        }
        if (request.getStatus() != null) {
            volume.setStatus(request.getStatus());
        }
        if (request.getFreePages() != null) {
            volume.setFreePages(request.getFreePages());
        }
        if (request.getZipFile() != null) {
            volume.setZipFile(request.getZipFile());
        }
        if (request.getZipFilePath() != null) {
            volume.setZipFilePath(request.getZipFilePath());
        }

        return volumeRepository.save(volume);
    }

    /**
     * 볼륨 삭제
     */
    @Transactional
    public void deleteVolume(User user, Long volumeId) {
        Publisher publisher = getPublisherByUser(user);

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        // 권한 확인
        if (!volume.getSeries().getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 볼륨에 대한 삭제 권한이 없습니다.");
        }

        Long seriesId = volume.getSeries().getId();
        volumeRepository.delete(volume);

        // 시리즈의 totalVolumes 업데이트
        updateSeriesTotalVolumes(seriesId);
    }

    // ========================================
    // Helper 메서드
    // ========================================

    /**
     * User로부터 Publisher 조회
     */
    private Publisher getPublisherByUser(User user) {
        if (user.getPublisher() == null) {
            throw new AccessDeniedException("출판사 계정이 아닙니다.");
        }
        return user.getPublisher();
    }

    /**
     * 시리즈의 총 볼륨 수 업데이트
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