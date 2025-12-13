package com.switchmanga.api.repository;

import com.switchmanga.api.entity.ReviewReport;
import com.switchmanga.api.entity.ReviewReport.ReportReason;
import com.switchmanga.api.entity.ReviewReport.ReporterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    // 리뷰 ID로 신고 목록 조회
    List<ReviewReport> findByReviewId(Long reviewId);

    // 리뷰별 신고 횟수
    long countByReviewId(Long reviewId);

    // 신고자가 해당 리뷰를 이미 신고했는지 확인
    boolean existsByReviewIdAndReporterIdAndReporterType(
            Long reviewId, Long reporterId, ReporterType reporterType);

    // 신고 사유별 카운트
    @Query("SELECT rr.reason, COUNT(rr) FROM ReviewReport rr " +
           "WHERE rr.review.id = :reviewId GROUP BY rr.reason")
    List<Object[]> countByReviewIdGroupByReason(@Param("reviewId") Long reviewId);

    // 특정 기간 내 신고 목록
    @Query("SELECT rr FROM ReviewReport rr " +
           "WHERE rr.createdAt >= :startDate ORDER BY rr.createdAt DESC")
    List<ReviewReport> findRecentReports(@Param("startDate") java.time.LocalDateTime startDate);

    // 출판사가 신고한 리뷰 목록
    List<ReviewReport> findByReporterIdAndReporterType(Long reporterId, ReporterType reporterType);
}
