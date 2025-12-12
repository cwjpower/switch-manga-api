package com.switchmanga.api.service;

import com.switchmanga.api.dto.publisher.*;
import com.switchmanga.api.dto.series.*;
import com.switchmanga.api.dto.volume.*;
import com.switchmanga.api.dto.order.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final OrderRepository orderRepository;

    // ========================================
    // Publisher 관련 메서드
    // ========================================

    public PublisherInfoResponse getMyInfo(User user) {
        Publisher publisher = getPublisherByUser(user);
        return PublisherInfoResponse.from(publisher);
    }

    @Transactional
    public PublisherInfoResponse updateMyInfo(User user, PublisherUpdateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        if (request.getName() != null) publisher.setName(request.getName());
        if (request.getNameEn() != null) publisher.setNameEn(request.getNameEn());
        if (request.getNameJp() != null) publisher.setNameJp(request.getNameJp());
        if (request.getLogo() != null) publisher.setLogo(request.getLogo());
        if (request.getEmail() != null) publisher.setEmail(request.getEmail());
        if (request.getPhone() != null) publisher.setPhone(request.getPhone());
        if (request.getWebsite() != null) publisher.setWebsite(request.getWebsite());
        if (request.getDescription() != null) publisher.setDescription(request.getDescription());

        Publisher saved = publisherRepository.save(publisher);
        return PublisherInfoResponse.from(saved);
    }

    public PublisherStatsResponse getMyStats(User user) {
        Publisher publisher = getPublisherByUser(user);
        Long publisherId = publisher.getId();

        long totalSeries = seriesRepository.countByPublisher_Id(publisherId);
        long totalVolumes = volumeRepository.countBySeries_Publisher_Id(publisherId);

        return PublisherStatsResponse.builder()
                .totalSeries(totalSeries)
                .totalVolumes(totalVolumes)
                .totalRevenue(0.0)
                .totalOrders(0L)
                .build();
    }

    // ========================================
    // Series 관련 메서드
    // ========================================

    public Map<String, Object> getMySeries(User user, int page, int size, String status, String search) {
        Publisher publisher = getPublisherByUser(user);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Series> seriesPage;

        if (search != null && !search.isEmpty()) {
            seriesPage = seriesRepository.findByPublisher_IdAndTitleContainingIgnoreCase(
                    publisher.getId(), search, pageable);
        } else if (status != null && !status.isEmpty()) {
            seriesPage = seriesRepository.findByPublisher_IdAndStatus(
                    publisher.getId(), status, pageable);
        } else {
            seriesPage = seriesRepository.findByPublisher_Id(publisher.getId(), pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", seriesPage.getContent().stream()
                .map(series -> {
                    SeriesListResponse response = SeriesListResponse.from(series);
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

    public SeriesDetailResponse getMySeriesDetail(User user, Long seriesId) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 시리즈에 대한 접근 권한이 없습니다.");
        }

        return SeriesDetailResponse.from(series);
    }

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

        if (request.getStatus() != null) {
            series.setStatus(request.getStatus());
        } else {
            series.setStatus("ONGOING");
        }

        if (request.getCategoryId() != null) {
            series.setCategoryId(request.getCategoryId());
        }

        return seriesRepository.save(series);
    }

    @Transactional
    public Series updateSeries(User user, Long seriesId, SeriesUpdateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 시리즈에 대한 수정 권한이 없습니다.");
        }

        if (request.getTitle() != null) series.setTitle(request.getTitle());
        if (request.getTitleEn() != null) series.setTitleEn(request.getTitleEn());
        if (request.getTitleJp() != null) series.setTitleJp(request.getTitleJp());
        if (request.getAuthor() != null) series.setAuthor(request.getAuthor());
        if (request.getDescription() != null) series.setDescription(request.getDescription());
        if (request.getCoverImage() != null) series.setCoverImage(request.getCoverImage());
        if (request.getStatus() != null) series.setStatus(request.getStatus());
        if (request.getCategoryId() != null) series.setCategoryId(request.getCategoryId());

        return seriesRepository.save(series);
    }

    @Transactional
    public void deleteSeries(User user, Long seriesId) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 시리즈에 대한 삭제 권한이 없습니다.");
        }

        seriesRepository.delete(series);
    }

    // ========================================
    // Volume 관련 메서드
    // ========================================

    /**
     * 볼륨 목록 조회
     * ✅ Controller와 파라미터 순서 일치: user, page, size, seriesId, search, status, sort
     */
    public Map<String, Object> getMyVolumes(User user, int page, int size, Long seriesId,
                                            String search, String status, String sort) {
        Publisher publisher = getPublisherByUser(user);

        // ✅ sort 파라미터에 따라 정렬 필드와 방향 결정
        String sortField = "createdAt";  // 기본값
        Sort.Direction direction = Sort.Direction.DESC;  // 기본값

        if ("asc".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
            sortField = "createdAt";
        } else if ("desc".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.DESC;
            sortField = "createdAt";
        } else if ("id".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
            sortField = "id";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<Volume> volumePage;

        if (seriesId != null) {
            Series series = seriesRepository.findById(seriesId)
                    .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + seriesId));

            if (!series.getPublisher().getId().equals(publisher.getId())) {
                throw new AccessDeniedException("이 시리즈에 대한 접근 권한이 없습니다.");
            }

            if (search != null && !search.isEmpty() && status != null && !status.isEmpty()) {
                volumePage = volumeRepository.findBySeriesIdAndTitleContainingAndStatus(
                        seriesId, search, status, pageable);
            } else if (search != null && !search.isEmpty()) {
                volumePage = volumeRepository.findBySeriesIdAndTitleContaining(
                        seriesId, search, pageable);
            } else if (status != null && !status.isEmpty()) {
                volumePage = volumeRepository.findBySeriesIdAndStatus(
                        seriesId, status, pageable);
            } else {
                volumePage = volumeRepository.findBySeriesId(seriesId, pageable);
            }
        } else {
            if (search != null && !search.isEmpty() && status != null && !status.isEmpty()) {
                volumePage = volumeRepository.findBySeries_Publisher_IdAndTitleContainingAndStatus(
                        publisher.getId(), search, status, pageable);
            } else if (search != null && !search.isEmpty()) {
                volumePage = volumeRepository.findBySeries_Publisher_IdAndTitleContaining(
                        publisher.getId(), search, pageable);
            } else if (status != null && !status.isEmpty()) {
                volumePage = volumeRepository.findBySeries_Publisher_IdAndStatus(
                        publisher.getId(), status, pageable);
            } else {
                volumePage = volumeRepository.findBySeries_Publisher_Id(publisher.getId(), pageable);
            }
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

    public VolumeDetailResponse getMyVolumeDetail(User user, Long volumeId) {
        Publisher publisher = getPublisherByUser(user);

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        if (!volume.getSeries().getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 볼륨에 대한 접근 권한이 없습니다.");
        }

        return VolumeDetailResponse.from(volume);
    }

    @Transactional
    public Volume createVolume(User user, VolumeCreateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + request.getSeriesId()));

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
        volume.setZipFile(request.getZipFile());
        volume.setZipFilePath(request.getZipFilePath());

        Volume saved = volumeRepository.save(volume);
        updateSeriesTotalVolumes(series.getId());

        return saved;
    }

    @Transactional
    public Volume updateVolume(User user, Long volumeId, VolumeUpdateRequest request) {
        Publisher publisher = getPublisherByUser(user);

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        if (!volume.getSeries().getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 볼륨에 대한 수정 권한이 없습니다.");
        }

        if (request.getVolumeNumber() != null) volume.setVolumeNumber(request.getVolumeNumber());
        if (request.getTitle() != null) volume.setTitle(request.getTitle());
        if (request.getTitleEn() != null) volume.setTitleEn(request.getTitleEn());
        if (request.getTitleJp() != null) volume.setTitleJp(request.getTitleJp());
        if (request.getCoverImage() != null) volume.setCoverImage(request.getCoverImage());
        if (request.getDescription() != null) volume.setDescription(request.getDescription());
        if (request.getPrice() != null) volume.setPrice(request.getPrice());
        if (request.getDiscountRate() != null) volume.setDiscountRate(request.getDiscountRate());
        if (request.getTotalPages() != null) volume.setTotalPages(request.getTotalPages());
        if (request.getPublishedDate() != null) volume.setPublishedDate(request.getPublishedDate());
        if (request.getIsFree() != null) volume.setIsFree(request.getIsFree());
        if (request.getStatus() != null) volume.setStatus(request.getStatus());
        if (request.getFreePages() != null) volume.setFreePages(request.getFreePages());
        if (request.getZipFile() != null) volume.setZipFile(request.getZipFile());
        if (request.getZipFilePath() != null) volume.setZipFilePath(request.getZipFilePath());

        return volumeRepository.save(volume);
    }

    @Transactional
    public void deleteVolume(User user, Long volumeId) {
        Publisher publisher = getPublisherByUser(user);

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        if (!volume.getSeries().getPublisher().getId().equals(publisher.getId())) {
            throw new AccessDeniedException("이 볼륨에 대한 삭제 권한이 없습니다.");
        }

        Long seriesId = volume.getSeries().getId();
        volumeRepository.delete(volume);
        updateSeriesTotalVolumes(seriesId);
    }

    // ========================================
    // Order 관련 메서드 (내 주문 내역)
    // ========================================

    /**
     * 내 주문 목록 조회
     */
    public Map<String, Object> getMyOrders(User user, int page, int size,
                                           String status, LocalDateTime startDate,
                                           LocalDateTime endDate, String sort,
                                           String keyword) {  // ✅ keyword 추가
        Publisher publisher = getPublisherByUser(user);

        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        Page<Order> orderPage;

        // ✅ 키워드 검색이 있으면 검색 쿼리 사용
        if (keyword != null && !keyword.trim().isEmpty()) {
            orderPage = orderRepository.searchByPublisherId(publisher.getId(), keyword.trim(), pageable);
        } else if (status != null && !status.isEmpty() && startDate != null && endDate != null) {
            orderPage = orderRepository.findByPublisherIdAndStatusAndDateRange(
                    publisher.getId(), status, startDate, endDate, pageable);
        } else if (status != null && !status.isEmpty()) {
            orderPage = orderRepository.findByPublisherIdAndStatus(
                    publisher.getId(), status, pageable);
        } else if (startDate != null && endDate != null) {
            orderPage = orderRepository.findByPublisherIdAndDateRange(
                    publisher.getId(), startDate, endDate, pageable);
        } else {
            orderPage = orderRepository.findByPublisherId(publisher.getId(), pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", orderPage.getContent().stream()
                .map(OrderListResponse::from)
                .collect(Collectors.toList()));
        result.put("page", orderPage.getNumber());
        result.put("size", orderPage.getSize());
        result.put("totalElements", orderPage.getTotalElements());
        result.put("totalPages", orderPage.getTotalPages());

        return result;
    }

    /**
     * 내 주문 통계 조회
     */
    public OrderStatsResponse getMyOrderStats(User user) {
        Publisher publisher = getPublisherByUser(user);
        Long publisherId = publisher.getId();

        Long totalOrders = orderRepository.countByPublisherId(publisherId);
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenueByPublisherIdAsBigDecimal(publisherId);
        BigDecimal thisMonthRevenue = orderRepository.calculateMonthlyRevenueByPublisherIdAsBigDecimal(publisherId);
        BigDecimal todayRevenue = orderRepository.calculateTodayRevenueByPublisherId(publisherId);

        return OrderStatsResponse.builder()
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .thisMonthRevenue(thisMonthRevenue != null ? thisMonthRevenue : BigDecimal.ZERO)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .build();
    }

    /**
     * 베스트셀러 조회
     */
    public java.util.List<BestsellerResponse> getMyBestsellers(User user, int limit) {
        Publisher publisher = getPublisherByUser(user);
        Pageable pageable = PageRequest.of(0, limit);

        java.util.List<Object[]> results = orderRepository.findBestsellersByPublisherId(publisher.getId(), pageable);

        java.util.List<BestsellerResponse> bestsellers = new ArrayList<>();
        int rank = 1;

        for (Object[] row : results) {
            Long volumeId = (Long) row[0];
            Long salesCount = (Long) row[1];
            BigDecimal revenue = (BigDecimal) row[2];

            // Volume 정보 조회
            Volume volume = volumeRepository.findById(volumeId).orElse(null);
            if (volume != null) {
                bestsellers.add(BestsellerResponse.builder()
                        .rank(rank++)
                        .volumeId(volumeId)
                        .volumeTitle(volume.getTitle())
                        .volumeNumber(volume.getVolumeNumber())
                        .seriesId(volume.getSeries().getId())
                        .seriesTitle(volume.getSeries().getTitle())
                        .coverImage(volume.getCoverImage())
                        .salesCount(salesCount)
                        .revenue(revenue)
                        .build());
            }
        }

        return bestsellers;
    }

    // ========================================
    // Helper 메서드
    // ========================================

    private Publisher getPublisherByUser(User user) {
        // ✅ 수정: publisherId로 직접 조회 (Lazy Loading 문제 해결)
        if (user.getPublisherId() == null) {
            throw new AccessDeniedException("출판사 계정이 아닙니다.");
        }
        return publisherRepository.findById(user.getPublisherId())
                .orElseThrow(() -> new AccessDeniedException("출판사를 찾을 수 없습니다."));
    }

    private void updateSeriesTotalVolumes(Long seriesId) {
        Series series = seriesRepository.findById(seriesId).orElse(null);
        if (series != null) {
            long count = volumeRepository.countBySeriesId(seriesId);
            series.setTotalVolumes((int) count);
            seriesRepository.save(series);
        }
    }
}