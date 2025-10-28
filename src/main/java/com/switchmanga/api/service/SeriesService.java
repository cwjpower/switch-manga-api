package com.switchmanga.api.service;

import com.switchmanga.api.dto.series.*;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.repository.SeriesRepository;
import com.switchmanga.api.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Series Service
 * - 기본 CRUD (Admin용)
 * - Publisher Portal용 메서드
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final PublisherRepository publisherRepository;

    // ========================================
    // 📋 기본 CRUD (Admin용)
    // ========================================

    public List<Series> getAllSeries() {
        return seriesRepository.findAll();
    }

    public Series getSeriesById(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다. ID: " + id));
    }

    public List<Series> getSeriesByPublisher(Long publisherId) {
        return seriesRepository.findByPublisherId(publisherId);
    }

    public List<Series> getActiveSeries() {
        return seriesRepository.findByActive(true);
    }

    public List<Series> getSeriesByStatus(String status) {
        return seriesRepository.findByStatus(status);
    }

    public List<Series> searchSeriesByTitle(String title) {
        return seriesRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Series> searchSeriesByAuthor(String author) {
        return seriesRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Transactional
    public Series createSeries(Series series, Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("출판사를 찾을 수 없습니다. ID: " + publisherId));
        series.setPublisher(publisher);
        return seriesRepository.save(series);
    }

    @Transactional
    public Series updateSeries(Long id, Series seriesDetails) {
        Series series = getSeriesById(id);
        series.setTitle(seriesDetails.getTitle());
        series.setTitleEn(seriesDetails.getTitleEn());
        series.setTitleJp(seriesDetails.getTitleJp());
        series.setAuthor(seriesDetails.getAuthor());
        series.setArtist(seriesDetails.getArtist());
        series.setCoverImage(seriesDetails.getCoverImage());
        series.setDescription(seriesDetails.getDescription());
        series.setStatus(seriesDetails.getStatus());
        series.setReleaseDate(seriesDetails.getReleaseDate());
        series.setActive(seriesDetails.getActive());
        return seriesRepository.save(series);
    }

    @Transactional
    public Series changePublisher(Long seriesId, Long newPublisherId) {
        Series series = getSeriesById(seriesId);
        Publisher newPublisher = publisherRepository.findById(newPublisherId)
                .orElseThrow(() -> new RuntimeException("출판사를 찾을 수 없습니다. ID: " + newPublisherId));
        series.setPublisher(newPublisher);
        return seriesRepository.save(series);
    }

    @Transactional
    public void deleteSeries(Long id) {
        Series series = getSeriesById(id);
        seriesRepository.delete(series);
    }

    // ========================================
    // 🔒 PUBLISHER PORTAL 전용 메서드
    // ========================================

    /**
     * 내 시리즈 목록 조회 (Portal용)
     */
    public List<SeriesListResponse> getMySeries(User user) {
        Publisher publisher = getPublisherFromUser(user);

        return seriesRepository.findByPublisherId(publisher.getId()).stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .map(SeriesListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 내 시리즈 생성 (Portal용)
     */
    @Transactional
    public SeriesDetailResponse createMySeries(User user, SeriesCreateRequest request) {
        Publisher publisher = getPublisherFromUser(user);

        // builder 대신 new + setter 사용
        Series series = new Series();
        series.setPublisher(publisher);
        series.setTitle(request.getTitle());
        series.setTitleEn(request.getTitleEn());
        series.setTitleJp(request.getTitleJp());
        series.setAuthor(request.getAuthor());
        series.setArtist(request.getArtist());
        series.setDescription(request.getDescription());
        series.setStatus(request.getStatus() != null ? request.getStatus() : "ONGOING");
        series.setCoverImage(request.getCoverImage());
        series.setActive(true);

        Series saved = seriesRepository.save(series);
        return SeriesDetailResponse.from(saved);
    }

    /**
     * 내 시리즈 상세 조회 (Portal용)
     */
    public SeriesDetailResponse getMySeriesDetail(User user, Long seriesId) {
        Series series = getSeriesById(seriesId);
        validateSeriesOwnership(user, series);

        return SeriesDetailResponse.from(series);
    }

    /**
     * 내 시리즈 수정 (Portal용)
     */
    @Transactional
    public SeriesDetailResponse updateMySeries(User user, Long seriesId, SeriesUpdateRequest request) {
        Series series = getSeriesById(seriesId);
        validateSeriesOwnership(user, series);

        updateSeriesFromRequest(series, request);

        return SeriesDetailResponse.from(series);
    }

    /**
     * 내 시리즈 삭제 (Portal용 - Soft Delete)
     */
    @Transactional
    public void deleteMySeries(User user, Long seriesId) {
        Series series = getSeriesById(seriesId);
        validateSeriesOwnership(user, series);

        series.setActive(false);
        seriesRepository.save(series);
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
     * 시리즈 소유권 검증
     */
    private void validateSeriesOwnership(User user, Series series) {
        Publisher publisher = getPublisherFromUser(user);

        if (!series.getPublisher().getId().equals(publisher.getId())) {
            throw new RuntimeException("해당 시리즈에 접근할 권한이 없습니다.");
        }
    }

    /**
     * Request로 Series 업데이트
     */
    private void updateSeriesFromRequest(Series series, SeriesUpdateRequest request) {
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
        if (request.getArtist() != null) {
            series.setArtist(request.getArtist());
        }
        if (request.getDescription() != null) {
            series.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            series.setStatus(request.getStatus());
        }
        if (request.getCoverImage() != null) {
            series.setCoverImage(request.getCoverImage());
        }
        if (request.getActive() != null) {
            series.setActive(request.getActive());
        }
    }
}