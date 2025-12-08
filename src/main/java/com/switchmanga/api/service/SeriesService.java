package com.switchmanga.api.service;

import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.repository.PublisherRepository;
import com.switchmanga.api.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final PublisherRepository publisherRepository;

    // ========================================
    // Controller에서 호출하는 메서드들 (11개)
    // ========================================

    /**
     * 전체 시리즈 목록 조회
     */
    public List<Series> getAllSeries() {
        return seriesRepository.findAll();
    }

    /**
     * 시리즈 ID로 조회
     */
    public Series getSeriesById(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + id));
    }

    /**
     * 출판사별 시리즈 목록 조회
     */
    public List<Series> getSeriesByPublisher(Long publisherId) {
        // ✅ 수정: findByPublisherId → findByPublisher_Id
        return seriesRepository.findByPublisher_Id(publisherId);
    }

    /**
     * 활성 시리즈 목록 조회 (status가 ONGOING인 것)
     */
    public List<Series> getActiveSeries() {
        return seriesRepository.findByStatus("ONGOING");
    }

    /**
     * 상태별 시리즈 목록 조회
     */
    public List<Series> getSeriesByStatus(String status) {
        return seriesRepository.findByStatus(status);
    }

    /**
     * 제목으로 시리즈 검색
     */
    public List<Series> searchSeriesByTitle(String title) {
        return seriesRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * 작가로 시리즈 검색
     */
    public List<Series> searchSeriesByAuthor(String author) {
        return seriesRepository.findByAuthorContainingIgnoreCase(author);
    }

    /**
     * 시리즈 생성
     */
    @Transactional
    public Series createSeries(Series series, Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new IllegalArgumentException("출판사를 찾을 수 없습니다: " + publisherId));

        series.setPublisher(publisher);

        // 기본값 설정
        if (series.getStatus() == null) {
            series.setStatus("ONGOING");
        }
        if (series.getTotalVolumes() == null) {
            series.setTotalVolumes(0);
        }

        return seriesRepository.save(series);
    }

    /**
     * 시리즈 수정
     */
    @Transactional
    public Series updateSeries(Long id, Series seriesDetails) {
        Series series = getSeriesById(id);

        if (seriesDetails.getTitle() != null) {
            series.setTitle(seriesDetails.getTitle());
        }
        if (seriesDetails.getTitleEn() != null) {
            series.setTitleEn(seriesDetails.getTitleEn());
        }
        if (seriesDetails.getTitleJp() != null) {
            series.setTitleJp(seriesDetails.getTitleJp());
        }
        if (seriesDetails.getAuthor() != null) {
            series.setAuthor(seriesDetails.getAuthor());
        }
        if (seriesDetails.getCoverImage() != null) {
            series.setCoverImage(seriesDetails.getCoverImage());
        }
        if (seriesDetails.getDescription() != null) {
            series.setDescription(seriesDetails.getDescription());
        }
        if (seriesDetails.getStatus() != null) {
            series.setStatus(seriesDetails.getStatus());
        }
        if (seriesDetails.getCategoryId() != null) {
            series.setCategoryId(seriesDetails.getCategoryId());
        }

        return seriesRepository.save(series);
    }

    /**
     * 시리즈 출판사 변경
     */
    @Transactional
    public Series changePublisher(Long seriesId, Long publisherId) {
        Series series = getSeriesById(seriesId);
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new IllegalArgumentException("출판사를 찾을 수 없습니다: " + publisherId));

        series.setPublisher(publisher);
        return seriesRepository.save(series);
    }

    /**
     * 시리즈 삭제
     */
    @Transactional
    public void deleteSeries(Long id) {
        Series series = getSeriesById(id);
        seriesRepository.delete(series);
    }

    // ========================================
    // 추가 유틸리티 메서드
    // ========================================

    /**
     * 출판사별 시리즈 개수 조회
     */
    public Long countByPublisherId(Long publisherId) {
        // ✅ 수정: countByPublisherId → countByPublisher_Id
        return seriesRepository.countByPublisher_Id(publisherId);
    }
}