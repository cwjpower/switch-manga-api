package com.switchmanga.api.service;

import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.repository.SeriesRepository;
import com.switchmanga.api.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final PublisherRepository publisherRepository;

    // 전체 시리즈 조회
    public List<Series> getAllSeries() {
        return seriesRepository.findAll();
    }

    // 시리즈 상세 조회
    public Series getSeriesById(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다. ID: " + id));
    }

    // 출판사별 시리즈 조회
    public List<Series> getSeriesByPublisher(Long publisherId) {
        return seriesRepository.findByPublisherId(publisherId);
    }

    // 활성화된 시리즈만 조회
    public List<Series> getActiveSeries() {
        return seriesRepository.findByActiveTrue();
    }

    // 상태별 시리즈 조회
    public List<Series> getSeriesByStatus(String status) {
        return seriesRepository.findByStatus(status);
    }

    // 제목으로 검색
    public List<Series> searchSeriesByTitle(String title) {
        return seriesRepository.findByTitleContaining(title);
    }

    // 작가로 검색
    public List<Series> searchSeriesByAuthor(String author) {
        return seriesRepository.findByAuthorContaining(author);
    }

    // 시리즈 생성
    @Transactional
    public Series createSeries(Series series, Long publisherId) {
        // 출판사 존재 확인
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("출판사를 찾을 수 없습니다. ID: " + publisherId));

        // 시리즈에 출판사 설정
        series.setPublisher(publisher);

        return seriesRepository.save(series);
    }

    // 시리즈 수정
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

    // 출판사 변경
    @Transactional
    public Series changePublisher(Long seriesId, Long newPublisherId) {
        Series series = getSeriesById(seriesId);
        Publisher newPublisher = publisherRepository.findById(newPublisherId)
                .orElseThrow(() -> new RuntimeException("출판사를 찾을 수 없습니다. ID: " + newPublisherId));

        series.setPublisher(newPublisher);

        return seriesRepository.save(series);
    }

    // 시리즈 삭제
    @Transactional
    public void deleteSeries(Long id) {
        Series series = getSeriesById(id);
        seriesRepository.delete(series);
    }
}