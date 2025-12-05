package com.switchmanga.api.service;

import com.switchmanga.api.dto.upload.VolumeUploadRequest;
import com.switchmanga.api.dto.upload.VolumeUploadResponse;
import com.switchmanga.api.entity.Series;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.repository.SeriesRepository;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VolumeUploadService {

    private final VolumeRepository volumeRepository;
    private final SeriesRepository seriesRepository;
    private final PageService pageService;

    private static final String UPLOAD_DIR = "uploads";

    /**
     * 볼륨 업로드 (Controller에서 호출하는 메서드)
     *
     * @param request 업로드 요청 정보
     * @param zipFile ZIP 파일 (페이지 이미지들)
     * @param coverFile 커버 이미지 파일
     * @return 업로드 응답
     */
    @Transactional
    public VolumeUploadResponse uploadVolume(VolumeUploadRequest request,
                                             MultipartFile zipFile,
                                             MultipartFile coverFile) {
        try {
            // 시리즈 조회
            Series series = seriesRepository.findById(request.getSeriesId())
                    .orElseThrow(() -> new IllegalArgumentException("시리즈를 찾을 수 없습니다: " + request.getSeriesId()));

            // 볼륨 생성
            Volume volume = new Volume();
            volume.setSeries(series);
            volume.setVolumeNumber(request.getVolumeNumber());
            volume.setTitle(request.getTitle());
            volume.setTitleEn(request.getTitleEn());
            volume.setTitleJp(request.getTitleJp());
            volume.setDescription(request.getDescription());
            volume.setPrice(request.getPrice() != null ? BigDecimal.valueOf(request.getPrice()) : BigDecimal.ZERO);

            // publishedDate: String → LocalDate 변환
            if (request.getPublishedDate() != null && !request.getPublishedDate().isEmpty()) {
                volume.setPublishedDate(LocalDate.parse(request.getPublishedDate()));
            } else {
                volume.setPublishedDate(LocalDate.now());
            }
            volume.setTotalPages(0);

            // 커버 이미지 처리
            String coverImageUrl = null;
            if (coverFile != null && !coverFile.isEmpty()) {
                coverImageUrl = saveCoverImage(series.getId(), request.getVolumeNumber(), coverFile);
                volume.setCoverImage(coverImageUrl);
            } else if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
                volume.setCoverImage(request.getCoverImage());
                coverImageUrl = request.getCoverImage();
            }

            Volume savedVolume = volumeRepository.save(volume);

            // ZIP 파일에서 페이지 추출
            int pageCount = 0;
            if (zipFile != null && !zipFile.isEmpty()) {
                try {
                    var pages = pageService.extractPagesFromZip(savedVolume.getId(), zipFile);
                    pageCount = pages.size();

                    // totalPages 업데이트
                    savedVolume.setTotalPages(pageCount);
                    volumeRepository.save(savedVolume);
                } catch (Exception e) {
                    log.error("ZIP 파일 처리 중 오류 발생", e);
                    return VolumeUploadResponse.failure("페이지 추출 실패: " + e.getMessage());
                }
            }

            // 시리즈 totalVolumes 업데이트
            updateSeriesTotalVolumes(series.getId());

            return VolumeUploadResponse.success(
                    savedVolume.getId(),
                    series.getId(),
                    savedVolume.getVolumeNumber(),
                    savedVolume.getTitle(),
                    pageCount,
                    coverImageUrl
            );

        } catch (Exception e) {
            log.error("볼륨 업로드 실패", e);
            return VolumeUploadResponse.failure(e.getMessage());
        }
    }

    /**
     * 볼륨 업로드 (2개 파라미터 - 오버로드)
     */
    @Transactional
    public VolumeUploadResponse uploadVolume(VolumeUploadRequest request, MultipartFile zipFile) {
        return uploadVolume(request, zipFile, null);
    }

    /**
     * 커버 이미지만 업로드
     */
    @Transactional
    public String uploadCoverImage(Long volumeId, MultipartFile coverFile) throws IOException {
        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        String coverUrl = saveCoverImage(volume.getSeries().getId(), volume.getVolumeNumber(), coverFile);
        volume.setCoverImage(coverUrl);
        volumeRepository.save(volume);

        return coverUrl;
    }

    // ========================================
    // Helper 메서드
    // ========================================

    /**
     * 커버 이미지 저장
     */
    private String saveCoverImage(Long seriesId, Integer volumeNumber, MultipartFile coverFile) throws IOException {
        String coverDir = UPLOAD_DIR + "/covers/" + seriesId;
        Path uploadPath = Paths.get(coverDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = getFileExtension(coverFile.getOriginalFilename());
        String fileName = String.format("vol_%d_%s.%s",
                volumeNumber,
                UUID.randomUUID().toString().substring(0, 8),
                extension);
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(coverFile.getInputStream(), filePath);

        return "/" + coverDir + "/" + fileName;
    }

    /**
     * 시리즈의 totalVolumes 업데이트
     */
    private void updateSeriesTotalVolumes(Long seriesId) {
        Series series = seriesRepository.findById(seriesId).orElse(null);
        if (series != null) {
            long count = volumeRepository.countBySeriesId(seriesId);
            series.setTotalVolumes((int) count);
            seriesRepository.save(series);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}