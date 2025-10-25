package com.switchmanga.api.service;

import com.switchmanga.api.entity.Series;
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
        // findByActiveTrue() → findByActive(true)로 수정
        return seriesRepository.findByActive(true);
    }

    public List<Series> getSeriesByStatus(String status) {
        return seriesRepository.findByStatus(status);
    }

    public List<Series> searchSeriesByTitle(String title) {
        // findByTitleContaining() → findByTitleContainingIgnoreCase()로 수정
        return seriesRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Series> searchSeriesByAuthor(String author) {
        // findByAuthorContaining() → findByAuthorContainingIgnoreCase()로 수정
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
}
