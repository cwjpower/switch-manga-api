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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.switchmanga.api.dto.response.RevenueStatsResponse;
import com.switchmanga.api.dto.response.RevenueTrendResponse;


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
     */
    public Map<String, Object> getMyVolumes(User user, int page, int size, Long seriesId,
                                            String search, String status, String sort) {
        Publisher publisher = getPublisherByUser(user);

        String sortField = "createdAt";
        Sort.Direction direction = Sort.Direction.DESC;

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
     * 내 주문 목록 조회 (다중 키워드 검색 지원)
     */
    public Map<String, Object> getMyOrders(User user, int page, int size,
                                           String status, LocalDateTime startDate,
                                           LocalDateTime endDate, String sort,
                                           String keyword) {
        Publisher publisher = getPublisherByUser(user);

        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        Page<Order> orderPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String[] keywords = keyword.trim().split("\\s+");

            if (keywords.length == 1) {
                orderPage = orderRepository.searchByPublisherId(publisher.getId(), keywords[0], pageable);
            } else {
                Set<Order> allOrders = new LinkedHashSet<>();
                for (String kw : keywords) {
                    if (!kw.isEmpty()) {
                        Page<Order> partialResult = orderRepository.searchByPublisherId(
                                publisher.getId(), kw, PageRequest.of(0, 1000)
                        );
                        allOrders.addAll(partialResult.getContent());
                    }
                }

                List<Order> sortedOrders = new ArrayList<>(allOrders);
                if ("desc".equalsIgnoreCase(sort)) {
                    sortedOrders.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                } else {
                    sortedOrders.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                }

                int start = page * size;
                int end = Math.min(start + size, sortedOrders.size());
                List<Order> pageContent = start < sortedOrders.size()
                        ? sortedOrders.subList(start, end)
                        : new ArrayList<>();

                orderPage = new PageImpl<>(pageContent, pageable, sortedOrders.size());
            }
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
    public List<BestsellerResponse> getMyBestsellers(User user, int limit) {
        Publisher publisher = getPublisherByUser(user);
        Pageable pageable = PageRequest.of(0, limit);

        List<Object[]> results = orderRepository.findBestsellersByPublisherId(publisher.getId(), pageable);

        List<BestsellerResponse> bestsellers = new ArrayList<>();
        int rank = 1;

        for (Object[] row : results) {
            Long volumeId = (Long) row[0];
            Long salesCount = (Long) row[1];
            BigDecimal revenue = (BigDecimal) row[2];

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

    // ========================================
    // 🆕 매출 추이 (Revenue Trend) - 차트용
    // ========================================

    public RevenueTrendResponse getRevenueTrend(User user, String period) {
        Publisher publisher = getPublisherByUser(user);
        Long publisherId = publisher.getId();

        LocalDate today = LocalDate.now();
        List<RevenueTrendResponse.TrendPoint> currentPoints = new ArrayList<>();
        List<RevenueTrendResponse.TrendPoint> previousPoints = new ArrayList<>();

        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MM/dd");

        switch (period != null ? period : "month") {
            case "today":
                for (int hour = 0; hour <= LocalDateTime.now().getHour(); hour++) {
                    LocalDateTime start = today.atTime(hour, 0);
                    LocalDateTime end = today.atTime(hour, 59, 59);
                    BigDecimal revenue = orderRepository.calculateRevenueByPublisherIdAndDateRangeAsBigDecimal(publisherId, start, end);
                    Long sales = orderRepository.countSalesByPublisherIdAndDateRange(publisherId, start, end);
                    currentPoints.add(RevenueTrendResponse.TrendPoint.builder()
                            .label(String.format("%02d:00", hour))
                            .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                            .sales(sales != null ? sales : 0L)
                            .build());
                }
                break;
            case "week":
                LocalDate weekStart = today.with(DayOfWeek.MONDAY);
                for (int i = 0; i <= (int) java.time.temporal.ChronoUnit.DAYS.between(weekStart, today); i++) {
                    LocalDate date = weekStart.plusDays(i);
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.atTime(23, 59, 59);
                    BigDecimal revenue = orderRepository.calculateRevenueByPublisherIdAndDateRangeAsBigDecimal(publisherId, start, end);
                    Long sales = orderRepository.countSalesByPublisherIdAndDateRange(publisherId, start, end);
                    currentPoints.add(RevenueTrendResponse.TrendPoint.builder()
                            .label(date.format(java.time.format.DateTimeFormatter.ofPattern("E")))
                            .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                            .sales(sales != null ? sales : 0L)
                            .build());
                }
                break;
            case "year":
                for (int month = 1; month <= today.getMonthValue(); month++) {
                    LocalDate monthStart = today.withMonth(month).withDayOfMonth(1);
                    LocalDate monthEnd = month == today.getMonthValue() ? today : monthStart.withDayOfMonth(monthStart.lengthOfMonth());
                    LocalDateTime start = monthStart.atStartOfDay();
                    LocalDateTime end = monthEnd.atTime(23, 59, 59);
                    BigDecimal revenue = orderRepository.calculateRevenueByPublisherIdAndDateRangeAsBigDecimal(publisherId, start, end);
                    Long sales = orderRepository.countSalesByPublisherIdAndDateRange(publisherId, start, end);
                    currentPoints.add(RevenueTrendResponse.TrendPoint.builder()
                            .label(month + "월")
                            .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                            .sales(sales != null ? sales : 0L)
                            .build());
                }
                break;
            case "month":
            default:
                for (int day = 1; day <= today.getDayOfMonth(); day++) {
                    LocalDate date = today.withDayOfMonth(day);
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.atTime(23, 59, 59);
                    BigDecimal revenue = orderRepository.calculateRevenueByPublisherIdAndDateRangeAsBigDecimal(publisherId, start, end);
                    Long sales = orderRepository.countSalesByPublisherIdAndDateRange(publisherId, start, end);
                    currentPoints.add(RevenueTrendResponse.TrendPoint.builder()
                            .label(date.format(dateFormatter))
                            .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                            .sales(sales != null ? sales : 0L)
                            .build());
                }
                break;
        }

        return RevenueTrendResponse.builder()
                .period(period != null ? period : "month")
                .current(currentPoints)
                .previous(previousPoints)
                .build();
    }


    // ========================================
    // 🆕 매출 현황 (Revenue)
    // ========================================

    /**
     * 내 매출 현황 조회
     * @param user 현재 사용자
     * @param period 기간 (today, week, month, year)
     * @param startDate 시작일 (custom 기간용)
     * @param endDate 종료일 (custom 기간용)
     */
    public RevenueStatsResponse getMyRevenue(User user, String period,
                                             LocalDate startDate, LocalDate endDate) {
        Publisher publisher = getPublisherByUser(user);
        Long publisherId = publisher.getId();

        // 1. 기간 계산
        LocalDateTime periodStart;
        LocalDateTime periodEnd;
        LocalDateTime prevPeriodStart;
        LocalDateTime prevPeriodEnd;
        String periodType = period != null ? period : "month";

        LocalDate today = LocalDate.now();

        switch (periodType) {
            case "today":
                periodStart = today.atStartOfDay();
                periodEnd = LocalDateTime.now();
                prevPeriodStart = today.minusDays(1).atStartOfDay();
                prevPeriodEnd = today.minusDays(1).atTime(LocalDateTime.now().toLocalTime());
                break;
            case "week":
                LocalDate weekStart = today.with(DayOfWeek.MONDAY);
                periodStart = weekStart.atStartOfDay();
                periodEnd = LocalDateTime.now();
                prevPeriodStart = weekStart.minusWeeks(1).atStartOfDay();
                prevPeriodEnd = weekStart.minusWeeks(1).plusDays(
                        java.time.temporal.ChronoUnit.DAYS.between(weekStart, today)
                ).atTime(LocalDateTime.now().toLocalTime());
                break;
            case "year":
                periodStart = today.withDayOfYear(1).atStartOfDay();
                periodEnd = LocalDateTime.now();
                prevPeriodStart = today.minusYears(1).withDayOfYear(1).atStartOfDay();
                prevPeriodEnd = today.minusYears(1).withDayOfYear(today.getDayOfYear())
                        .atTime(LocalDateTime.now().toLocalTime());
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("custom 기간은 startDate와 endDate가 필요합니다");
                }
                periodStart = startDate.atStartOfDay();
                periodEnd = endDate.atTime(23, 59, 59);
                prevPeriodStart = null;
                prevPeriodEnd = null;
                break;
            case "month":
            default:
                periodType = "month";
                periodStart = today.withDayOfMonth(1).atStartOfDay();
                periodEnd = LocalDateTime.now();
                prevPeriodStart = today.minusMonths(1).withDayOfMonth(1).atStartOfDay();
                prevPeriodEnd = today.minusMonths(1).withDayOfMonth(
                        Math.min(today.getDayOfMonth(), today.minusMonths(1).lengthOfMonth())
                ).atTime(LocalDateTime.now().toLocalTime());
                break;
        }

        // 2. 현재 기간 데이터
        BigDecimal currentRevenue = orderRepository.calculateRevenueByPublisherIdAndDateRangeAsBigDecimal(
                publisherId, periodStart, periodEnd);
        Long currentSales = orderRepository.countSalesByPublisherIdAndDateRange(
                publisherId, periodStart, periodEnd);

        if (currentRevenue == null) currentRevenue = BigDecimal.ZERO;
        if (currentSales == null) currentSales = 0L;

        // 3. 이전 기간 데이터 (비교용)
        BigDecimal previousRevenue = BigDecimal.ZERO;
        Long previousSales = 0L;

        if (prevPeriodStart != null && prevPeriodEnd != null) {
            previousRevenue = orderRepository.calculateRevenueByPublisherIdAndDateRangeAsBigDecimal(
                    publisherId, prevPeriodStart, prevPeriodEnd);
            previousSales = orderRepository.countSalesByPublisherIdAndDateRange(
                    publisherId, prevPeriodStart, prevPeriodEnd);

            if (previousRevenue == null) previousRevenue = BigDecimal.ZERO;
            if (previousSales == null) previousSales = 0L;
        }

        // 4. 기본 계산
        BigDecimal netRevenue = currentRevenue.multiply(BigDecimal.valueOf(0.7))
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal platformFee = currentRevenue.multiply(BigDecimal.valueOf(0.3))
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal averagePrice = currentSales > 0
                ? currentRevenue.divide(BigDecimal.valueOf(currentSales), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String revenueChangeRate = RevenueStatsResponse.calculateChangeRate(currentRevenue, previousRevenue);
        String salesChangeRate = RevenueStatsResponse.calculateChangeRate(currentSales, previousSales);

        // 4-1. 🆕 새로운 지표 계산
        Long newCustomers = orderRepository.countNewCustomersByPublisherIdAndDateRange(
                publisherId, periodStart, periodEnd);
        Long totalCustomers = orderRepository.countTotalCustomersByPublisherIdAndDateRange(
                publisherId, periodStart, periodEnd);
        Long repeatCustomers = orderRepository.countRepeatCustomersByPublisherIdAndDateRange(
                publisherId, periodStart, periodEnd);
        Long totalViewCount = orderRepository.sumViewCountByPublisherId(publisherId);

        if (newCustomers == null) newCustomers = 0L;
        if (totalCustomers == null) totalCustomers = 0L;
        if (repeatCustomers == null) repeatCustomers = 0L;
        if (totalViewCount == null) totalViewCount = 0L;

        // 재구매율 계산 (재구매 고객 / 전체 고객 * 100)
        Double repeatRate = totalCustomers > 0
                ? (repeatCustomers * 100.0) / totalCustomers
                : 0.0;

        // 구매 전환율 계산 (판매 수 / 조회수 * 100)
        Double conversionRate = totalViewCount > 0
                ? (currentSales * 100.0) / totalViewCount
                : 0.0;

        // 5. 시리즈별 매출 Top 10
        List<Object[]> seriesData = orderRepository.findSeriesRevenueByPublisherIdAndDateRange(
                publisherId, periodStart, periodEnd, PageRequest.of(0, 10));

        List<RevenueStatsResponse.SeriesRevenue> topSeries = new ArrayList<>();
        int rank = 1;
        for (Object[] row : seriesData) {
            Long seriesId = (Long) row[0];
            String title = (String) row[1];
            String coverImage = (String) row[2];
            Long salesCount = (Long) row[3];
            BigDecimal revenue = (BigDecimal) row[4];

            double percentage = currentRevenue.compareTo(BigDecimal.ZERO) > 0
                    ? revenue.multiply(BigDecimal.valueOf(100))
                    .divide(currentRevenue, 1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            topSeries.add(RevenueStatsResponse.SeriesRevenue.builder()
                    .rank(rank++)
                    .seriesId(seriesId)
                    .seriesTitle(title)
                    .coverImage(coverImage)
                    .salesCount(salesCount)
                    .revenue(revenue)
                    .percentage(percentage)
                    .build());
        }

        // 6. 응답 생성
        return RevenueStatsResponse.builder()
                .period(RevenueStatsResponse.PeriodInfo.builder()
                        .type(periodType)
                        .startDate(periodStart.toLocalDate())
                        .endDate(periodEnd.toLocalDate())
                        .build())
                .summary(RevenueStatsResponse.SummaryStats.builder()
                        .totalRevenue(currentRevenue)
                        .netRevenue(netRevenue)
                        .totalSales(currentSales)
                        .averagePrice(averagePrice)
                        .revenueChangeRate(revenueChangeRate)
                        .salesChangeRate(salesChangeRate)
                        .previousRevenue(previousRevenue)
                        .previousSales(previousSales)
                        // 🆕 새로운 지표들
                        .newCustomers(newCustomers)
                        .totalCustomers(totalCustomers)
                        .repeatRate(Math.round(repeatRate * 10.0) / 10.0)
                        .conversionRate(Math.round(conversionRate * 100.0) / 100.0)
                        .totalViewCount(totalViewCount)
                        .build())
                .distribution(RevenueStatsResponse.RevenueDistribution.builder()
                        .totalRevenue(currentRevenue)
                        .publisherShare(netRevenue)
                        .platformFee(platformFee)
                        .shareRatio(70)
                        .build())
                .topSeries(topSeries)
                .build();
    }

}