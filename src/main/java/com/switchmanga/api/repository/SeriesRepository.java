package com.switchmanga.api.repository;

import com.switchmanga.api.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    List<Series> findByPublisherId(Long publisherId);
    List<Series> findByActiveTrue();
    List<Series> findByStatus(String status);
    List<Series> findByTitleContaining(String title);
    List<Series> findByAuthorContaining(String author);
    List<Series> findByPublisherIdAndStatus(Long publisherId, String status);
}