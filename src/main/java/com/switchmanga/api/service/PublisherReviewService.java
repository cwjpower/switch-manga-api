package com.switchmanga.api.service;

import com.switchmanga.api.dto.review.*;
import com.switchmanga.api.entity.*;
import com.switchmanga.api.entity.ReviewReport.ReportReason;
import com.switchmanga.api.entity.ReviewReport.ReporterType;
import com.switchmanga.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PublisherReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewReplyRepository reviewReplyRepository;

    @Autowired
    private ReviewReportRepository reviewReportRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private VolumeRepository volumeRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    // 베스트 리뷰 기준 좋아요 수 (기본값)
    private static final int BEST_REVIEW_LIKE_COUNT = 10;

    // ==================== 리뷰 목록 조회 ====================

    /**
     * 출판사의 리뷰 목록 조회
     */
    public Page<PublisherReviewResponse> getPublisherReviews(
            Long publisherId,
            Long seriesId,
            Long volumeId,
            Integer rating,
            String status,  // all, unanswered, answered
            String keyword,
            Pageable pageable) {

        // ✅ 디버그 로그 추가
        System.out.println("===== getPublisherReviews DEBUG =====");
        System.out.println("publisherId: " + publisherId);
        System.out.println("seriesId: " + seriesId);
        System.out.println("volumeId: " + volumeId);
        // ===== DEBUG =====
        System.out.println("===== getPublisherReviews DEBUG =====");
        System.out.println("publisherId: " + publisherId);

        // 1. 출판사의 볼륨 ID 목록 조회
        List<Long> volumeIds;
        
        if (volumeId != null) {
            // 특정 볼륨 필터
            volumeIds = Collections.singletonList(volumeId);
        } else if (seriesId != null) {
            // 특정 시리즈의 볼륨들
            volumeIds = volumeRepository.findBySeriesId(seriesId)
                    .stream()
                    .map(Volume::getId)
                    .collect(Collectors.toList());
        } else {
            // 출판사의 모든 볼륨
            volumeIds = volumeRepository.findBySeries_Publisher_Id(publisherId)
                    .stream()
                    .map(Volume::getId)
                    .collect(Collectors.toList());
        }

        // ✅ 디버그 로그 추가
        System.out.println("volumeIds: " + volumeIds);
        System.out.println("volumeIds.size(): " + volumeIds.size());

        if (volumeIds.isEmpty()) {
            System.out.println("⚠️ volumeIds is EMPTY! Returning empty page.");
            return Page.empty(pageable);
        }

        if (volumeIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. 리뷰 조회
        Page<Review> reviewPage = reviewRepository.findByVolumeIdIn(volumeIds, pageable);

        // 3. 답글 정보 조회 (N+1 방지)
        List<Long> reviewIds = reviewPage.getContent().stream()
                .map(Review::getId)
                .collect(Collectors.toList());
        
        Map<Long, ReviewReply> replyMap = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            replyMap = reviewReplyRepository.findByReviewIdIn(reviewIds)
                    .stream()
                    .collect(Collectors.toMap(r -> r.getReview().getId(), r -> r));
        }

        // 4. 필터링 및 변환
        final Map<Long, ReviewReply> finalReplyMap = replyMap;
        List<PublisherReviewResponse> responses = reviewPage.getContent().stream()
                .filter(review -> filterByStatus(review, finalReplyMap.get(review.getId()), status))
                .filter(review -> filterByRating(review, rating))
                .filter(review -> filterByKeyword(review, keyword))
                .map(review -> convertToResponse(review, finalReplyMap.get(review.getId())))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, reviewPage.getTotalElements());
    }

    /**
     * 리뷰 통계 조회
     */
    public PublisherReviewStatsResponse getPublisherReviewStats(Long publisherId) {
        
        // 출판사의 볼륨 ID 목록
        List<Long> volumeIds = volumeRepository.findBySeries_Publisher_Id(publisherId)
                .stream()
                .map(Volume::getId)
                .collect(Collectors.toList());

        if (volumeIds.isEmpty()) {
            return createEmptyStats();
        }

        PublisherReviewStatsResponse stats = new PublisherReviewStatsResponse();

        // 1. 전체 통계
        List<Review> allReviews = reviewRepository.findByVolumeIdIn(volumeIds);
        stats.setTotalReviews((long) allReviews.size());

        // 2. 평균 별점
        double avgRating = allReviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        stats.setAverageRating(Math.round(avgRating * 10) / 10.0);

        // 3. 이번 달 리뷰
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        long thisMonthCount = allReviews.stream()
                .filter(r -> r.getCreatedAt().isAfter(startOfMonth))
                .count();
        stats.setThisMonthReviews(thisMonthCount);

        // 4. 지난 달 리뷰 (추이용)
        LocalDateTime startOfLastMonth = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = YearMonth.now().atDay(1).atStartOfDay();
        long lastMonthCount = allReviews.stream()
                .filter(r -> r.getCreatedAt().isAfter(startOfLastMonth) && r.getCreatedAt().isBefore(endOfLastMonth))
                .count();
        stats.setRecentTrend(new PublisherReviewStatsResponse.TrendInfo(thisMonthCount, lastMonthCount));

        // 5. 미답변 리뷰 수
        List<Long> reviewIds = allReviews.stream()
                .map(Review::getId)
                .collect(Collectors.toList());
        
        long answeredCount = 0;
        if (!reviewIds.isEmpty()) {
            Set<Long> answeredReviewIds = reviewReplyRepository.findByReviewIdIn(reviewIds)
                    .stream()
                    .map(r -> r.getReview().getId())
                    .collect(Collectors.toSet());
            answeredCount = answeredReviewIds.size();
        }
        stats.setUnansweredReviews((long) reviewIds.size() - answeredCount);

        // 6. 베스트 리뷰 수
        long bestCount = allReviews.stream()
                .filter(r -> r.getLikeCount() != null && r.getLikeCount() >= BEST_REVIEW_LIKE_COUNT)
                .count();
        stats.setBestReviewCount(bestCount);

        // 7. 별점 분포 (Double → Integer 변환)
        Map<Integer, Long> distribution = allReviews.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRating().intValue(),  // Double → Integer
                        Collectors.counting()
                ));
        // 1~5 모두 포함하도록
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }
        stats.setRatingDistribution(distribution);

        return stats;
    }

    // ==================== 답글 관련 ====================

    /**
     * 답글 작성
     */
    @Transactional
    public ReviewReply createReply(Long publisherId, Long reviewId, String content) {
        
        // 1. 리뷰 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다"));

        // 2. 출판사 확인
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("출판사를 찾을 수 없습니다"));

        // 3. 권한 확인 (해당 출판사의 볼륨인지)
        Volume volume = review.getVolume();
        if (!volume.getSeries().getPublisher().getId().equals(publisherId)) {
            throw new RuntimeException("해당 리뷰에 답글을 작성할 권한이 없습니다");
        }

        // 4. 기존 답글 확인
        if (reviewReplyRepository.existsByReviewId(reviewId)) {
            throw new RuntimeException("이미 답글이 존재합니다");
        }

        // 5. 답글 생성
        ReviewReply reply = new ReviewReply(review, publisher, content);
        return reviewReplyRepository.save(reply);
    }

    /**
     * 답글 수정
     */
    @Transactional
    public ReviewReply updateReply(Long publisherId, Long reviewId, String content) {
        
        ReviewReply reply = reviewReplyRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다"));

        // 권한 확인
        if (!reply.getPublisher().getId().equals(publisherId)) {
            throw new RuntimeException("수정 권한이 없습니다");
        }

        reply.setContent(content);
        return reviewReplyRepository.save(reply);
    }

    /**
     * 답글 삭제
     */
    @Transactional
    public void deleteReply(Long publisherId, Long reviewId) {
        
        ReviewReply reply = reviewReplyRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다"));

        // 권한 확인
        if (!reply.getPublisher().getId().equals(publisherId)) {
            throw new RuntimeException("삭제 권한이 없습니다");
        }

        reviewReplyRepository.delete(reply);
    }

    // ==================== 신고 관련 ====================

    /**
     * 리뷰 신고
     */
    @Transactional
    public ReviewReport reportReview(Long publisherId, Long reviewId, 
                                      String reason, String description) {
        
        // 1. 리뷰 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다"));

        // 2. 중복 신고 확인
        if (reviewReportRepository.existsByReviewIdAndReporterIdAndReporterType(
                reviewId, publisherId, ReporterType.PUBLISHER)) {
            throw new RuntimeException("이미 신고한 리뷰입니다");
        }

        // 3. 신고 생성
        ReportReason reportReason;
        try {
            reportReason = ReportReason.valueOf(reason.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("잘못된 신고 사유입니다");
        }

        ReviewReport report = new ReviewReport(
                review, publisherId, ReporterType.PUBLISHER, reportReason, description);
        ReviewReport savedReport = reviewReportRepository.save(report);

        // 4. 리뷰의 신고 횟수 증가
        Integer currentReportCount = review.getReportCount();
        if (currentReportCount == null) {
            currentReportCount = 0;
        }
        review.setReportCount(currentReportCount + 1);

        // 5. 자동 숨김 체크 (3회 이상)
        Boolean isHidden = review.getIsHidden();
        if (isHidden == null) {
            isHidden = false;
        }
        
        if (review.getReportCount() >= 3 && !isHidden) {
            review.setIsHidden(true);
            review.setHiddenReason("신고 누적 자동 숨김");
            review.setHiddenAt(LocalDateTime.now());
            // TODO: 작성자에게 경고 처리
        }

        reviewRepository.save(review);
        return savedReport;
    }

    // ==================== Helper Methods ====================

    private boolean filterByStatus(Review review, ReviewReply reply, String status) {
        if (status == null || "all".equalsIgnoreCase(status)) {
            return true;
        }
        if ("unanswered".equalsIgnoreCase(status)) {
            return reply == null;
        }
        if ("answered".equalsIgnoreCase(status)) {
            return reply != null;
        }
        return true;
    }

    private boolean filterByRating(Review review, Integer rating) {
        if (rating == null) {
            return true;
        }
        // Double을 Integer로 비교
        return review.getRating().intValue() == rating;
    }

    private boolean filterByKeyword(Review review, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lowerKeyword = keyword.toLowerCase();
        
        // content 검색
        boolean contentMatch = review.getContent() != null && 
                review.getContent().toLowerCase().contains(lowerKeyword);
        
        // username 검색 (nickname이 아닌 username 사용)
        boolean usernameMatch = review.getUser().getUsername() != null && 
                review.getUser().getUsername().toLowerCase().contains(lowerKeyword);
        
        return contentMatch || usernameMatch;
    }

    private PublisherReviewResponse convertToResponse(Review review, ReviewReply reply) {
        PublisherReviewResponse response = new PublisherReviewResponse();
        
        response.setId(review.getId());
        response.setRating(review.getRating());  // Double 그대로 사용
        response.setContent(review.getContent());
        response.setLikeCount(review.getLikeCount());
        response.setCreatedAt(review.getCreatedAt());

        // 숨김 여부 (null 체크)
        Boolean isHidden = review.getIsHidden();
        response.setIsHidden(isHidden != null ? isHidden : false);

        // 작성자 정보 (username 사용)
        User user = review.getUser();
        response.setUserId(user.getId());
        response.setUserName(user.getUsername());  // nickname → username
        response.setUserProfileImage(user.getProfileImage());

        // 볼륨/시리즈 정보
        Volume volume = review.getVolume();
        response.setVolumeId(volume.getId());
        response.setVolumeTitle(volume.getTitle());
        response.setVolumeCoverImage(volume.getCoverImage());
        
        Series series = volume.getSeries();
        response.setSeriesId(series.getId());
        response.setSeriesTitle(series.getTitle());

        // 답글 정보
        if (reply != null) {
            PublisherReviewResponse.ReplyInfo replyInfo = new PublisherReviewResponse.ReplyInfo(
                    reply.getId(),
                    reply.getContent(),
                    reply.getCreatedAt(),
                    reply.getUpdatedAt()
            );
            response.setReply(replyInfo);
        }

        // 베스트 리뷰 여부
        Integer likeCount = review.getLikeCount();
        response.setIsBestReview(likeCount != null && likeCount >= BEST_REVIEW_LIKE_COUNT);

        return response;
    }

    private PublisherReviewStatsResponse createEmptyStats() {
        PublisherReviewStatsResponse stats = new PublisherReviewStatsResponse();
        stats.setAverageRating(0.0);
        stats.setTotalReviews(0L);
        stats.setThisMonthReviews(0L);
        stats.setUnansweredReviews(0L);
        stats.setBestReviewCount(0L);
        
        Map<Integer, Long> emptyDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            emptyDistribution.put(i, 0L);
        }
        stats.setRatingDistribution(emptyDistribution);
        stats.setRecentTrend(new PublisherReviewStatsResponse.TrendInfo(0L, 0L));
        
        return stats;
    }
}
