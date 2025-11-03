package com.switchmanga.api.service;

import com.switchmanga.api.dto.upload.*;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.exception.FileValidationException;
import com.switchmanga.api.repository.SeriesRepository;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolumeUploadService {

    private final FileUploadService fileUploadService;
    private final VolumeRepository volumeRepository;
    private final SeriesRepository seriesRepository;

    /**
     * 통합 Volume 업로드
     * - 표지 이미지 업로드
     * - ZIP 파일 업로드 (선택)
     * - DB 저장
     */
    @Transactional
    public VolumeUploadResponse uploadVolume(
            VolumeUploadRequest request,
            MultipartFile coverImage,
            MultipartFile zipFile) {

        Path uploadDir = null;

        try {
            log.info("Starting volume upload: {}", request.getTitle());

            // 1. 시리즈 존재 확인
            Series series = seriesRepository.findById(request.getSeriesId())
                    .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + request.getSeriesId()));

            // 2. 표지 이미지 업로드 (필수)
            ImageUploadResponse imageResponse = fileUploadService.uploadCoverImage(coverImage);
            if (!imageResponse.isSuccess()) {
                throw new FileValidationException("표지 이미지 업로드 실패: " + imageResponse.getErrorMessage());
            }

            log.info("Cover image uploaded: {}", imageResponse.getFilePath());

            // 3. ZIP 파일 업로드 (선택)
            String pagesDirectory = null;
            Integer pageCount = 0;
            Boolean hasAvf = false;
            String avfFilePath = null;

            if (zipFile != null && !zipFile.isEmpty()) {
                ZipUploadResponse zipResponse = fileUploadService.uploadAndExtractZip(zipFile);

                if (!zipResponse.isSuccess()) {
                    // ZIP 업로드 실패해도 표지는 저장됨 - 롤백 필요
                    throw new FileValidationException("ZIP 파일 업로드 실패: " + zipResponse.getErrorMessage());
                }

                pagesDirectory = zipResponse.getPagesDirectory();
                pageCount = zipResponse.getExtractedFileCount();
                hasAvf = zipResponse.isHasAvfFile();
                avfFilePath = zipResponse.getAvfFilePath();

                log.info("ZIP file uploaded and extracted: {} files", pageCount);
            }

            // 4. Volume 엔티티 생성
            Volume volume = Volume.builder()
                    .series(series)
                    .volumeNumber(request.getVolumeNumber())
                    .title(request.getTitle())
                    .titleEn(request.getTitleEn())
                    .titleJp(request.getTitleJp())
                    .author(request.getAuthor())
                    .authorEn(request.getAuthorEn())
                    .authorJp(request.getAuthorJp())
                    .price(BigDecimal.valueOf(request.getPrice()))
                    .discountRate(request.getDiscountRate() != null ? request.getDiscountRate() : 0)
                    .isbn(request.getIsbn())
                    .publicationDate(parseDate(request.getPublishedDate()))  // publishedDate → publicationDate
                    .description(request.getDescription())
                    .descriptionEn(request.getDescriptionEn())
                    .descriptionJp(request.getDescriptionJp())
                    .coverImage(imageResponse.getFilePath())
                    .pagesDirectory(pagesDirectory)
                    .pageCount(pageCount)
                    .previewPages(request.getPreviewPages())
                    .ageRating(request.getAgeRating())
                    .freeTrialDays(request.getFreeTrialDays())
                    .isFree(request.getIsFree() != null && request.getIsFree())
                    .hasAction(hasAvf)
                    .avfFilePath(avfFilePath)
                    .active(true)
                    .build();

            // 5. DB 저장
            Volume savedVolume = volumeRepository.save(volume);

            log.info("Volume saved to DB: ID={}, Title={}", savedVolume.getId(), savedVolume.getTitle());

            // 6. 성공 응답 반환
            return VolumeUploadResponse.success(
                    savedVolume.getId(),
                    savedVolume.getTitle(),
                    savedVolume.getVolumeNumber(),
                    savedVolume.getCoverImage(),
                    savedVolume.getPagesDirectory(),
                    savedVolume.getPageCount(),
                    savedVolume.getHasAction(),
                    savedVolume.getAvfFilePath(),
                    series.getId(),
                    series.getTitle()
            );

        } catch (Exception e) {
            log.error("Volume upload failed", e);

            // 실패 시 업로드된 파일 삭제 (롤백)
            if (uploadDir != null) {
                fileUploadService.deleteUploadDirectory(uploadDir);
            }

            return VolumeUploadResponse.failure("Volume 업로드 실패: " + e.getMessage());
        }
    }

    /**
     * 날짜 문자열을 LocalDateTime으로 변환
     */
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            log.warn("Failed to parse date: {}", dateString);
            return null;
        }
    }
}