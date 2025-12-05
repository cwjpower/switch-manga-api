package com.switchmanga.api.service;

import com.switchmanga.api.entity.Page;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.repository.PageRepository;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PageService {

    private final PageRepository pageRepository;
    private final VolumeRepository volumeRepository;

    private static final String UPLOAD_DIR = "uploads/pages";

    // ========================================
    // Controller에서 호출하는 메서드들
    // ========================================

    /**
     * Volume의 페이지 목록 조회
     */
    public List<Page> getPagesByVolume(Long volumeId) {
        return pageRepository.findByVolumeIdOrderByPageNumberAsc(volumeId);
    }

    /**
     * 페이지 ID로 조회
     */
    public Page getPageById(Long id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("페이지를 찾을 수 없습니다: " + id));
    }

    /**
     * Volume의 페이지 개수 조회
     */
    public Long getPageCountByVolume(Long volumeId) {
        return pageRepository.countByVolumeId(volumeId);
    }

    /**
     * 페이지 생성
     */
    @Transactional
    public Page createPage(Page page, Long volumeId) {
        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        page.setVolume(volume);
        Page saved = pageRepository.save(page);

        // Volume의 totalPages 업데이트
        updateVolumeTotalPages(volumeId);

        return saved;
    }

    /**
     * 페이지 수정
     */
    @Transactional
    public Page updatePage(Long id, Page pageDetails) {
        Page page = getPageById(id);

        page.setPageNumber(pageDetails.getPageNumber());
        page.setImageUrl(pageDetails.getImageUrl());
        page.setThumbnailUrl(pageDetails.getThumbnailUrl());
        page.setWidth(pageDetails.getWidth());
        page.setHeight(pageDetails.getHeight());
        page.setFileSize(pageDetails.getFileSize());

        return pageRepository.save(page);
    }

    /**
     * 페이지 순서 변경
     */
    @Transactional
    public Page updatePageOrder(Long id, Integer newPageNumber) {
        Page page = getPageById(id);
        page.setPageNumber(newPageNumber);
        return pageRepository.save(page);
    }

    /**
     * 페이지 삭제
     */
    @Transactional
    public void deletePage(Long id) {
        Page page = getPageById(id);
        Long volumeId = page.getVolume().getId();

        pageRepository.delete(page);

        // Volume의 totalPages 업데이트
        updateVolumeTotalPages(volumeId);
    }

    /**
     * Volume의 모든 페이지 삭제
     */
    @Transactional
    public void deleteAllPagesByVolume(Long volumeId) {
        pageRepository.deleteByVolumeId(volumeId);

        // Volume의 totalPages 업데이트
        Volume volume = volumeRepository.findById(volumeId).orElse(null);
        if (volume != null) {
            volume.setTotalPages(0);
            volumeRepository.save(volume);
        }
    }

    /**
     * ZIP 파일 업로드 및 페이지 추출
     */
    @Transactional
    public List<Page> uploadZipAndExtractPages(Long volumeId, MultipartFile zipFile) throws IOException {
        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new IllegalArgumentException("볼륨을 찾을 수 없습니다: " + volumeId));

        // 기존 페이지 삭제
        pageRepository.deleteByVolumeId(volumeId);

        // 저장 디렉토리 생성
        String volumeDir = UPLOAD_DIR + "/" + volumeId;
        Path uploadPath = Paths.get(volumeDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        List<Page> extractedPages = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            int pageNumber = 1;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && isImageFile(entry.getName())) {
                    // 파일명 생성
                    String extension = getFileExtension(entry.getName());
                    String fileName = String.format("page_%03d.%s", pageNumber, extension);
                    Path filePath = uploadPath.resolve(fileName);

                    // 파일 저장
                    Files.copy(zis, filePath);

                    // Page 엔티티 생성
                    Page page = new Page();
                    page.setVolume(volume);
                    page.setPageNumber(pageNumber);
                    page.setImageUrl("/" + volumeDir + "/" + fileName);
                    page.setFileSize((int) entry.getSize());

                    extractedPages.add(page);
                    pageNumber++;
                }
                zis.closeEntry();
            }
        }

        // 페이지 번호순 정렬
        extractedPages.sort(Comparator.comparing(Page::getPageNumber));

        // 저장
        List<Page> savedPages = pageRepository.saveAll(extractedPages);

        // Volume의 totalPages 업데이트
        volume.setTotalPages(extractedPages.size());
        volumeRepository.save(volume);

        return savedPages;
    }

    // ========================================
    // 기존 메서드들 (호환성 유지)
    // ========================================

    public List<Page> findByVolumeId(Long volumeId) {
        return getPagesByVolume(volumeId);
    }

    public Page findById(Long id) {
        return getPageById(id);
    }

    public Page findByVolumeIdAndPageNumber(Long volumeId, Integer pageNumber) {
        return pageRepository.findByVolumeIdAndPageNumber(volumeId, pageNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "페이지를 찾을 수 없습니다. volumeId: " + volumeId + ", pageNumber: " + pageNumber));
    }

    @Transactional
    public Page create(Page page) {
        return pageRepository.save(page);
    }

    @Transactional
    public List<Page> createAll(List<Page> pages) {
        return pageRepository.saveAll(pages);
    }

    @Transactional
    public void delete(Long id) {
        deletePage(id);
    }

    @Transactional
    public void deleteByVolumeId(Long volumeId) {
        deleteAllPagesByVolume(volumeId);
    }

    @Transactional
    public List<Page> extractPagesFromZip(Long volumeId, MultipartFile zipFile) throws IOException {
        return uploadZipAndExtractPages(volumeId, zipFile);
    }

    public Long countByVolumeId(Long volumeId) {
        return getPageCountByVolume(volumeId);
    }

    // ========================================
    // Helper 메서드
    // ========================================

    private void updateVolumeTotalPages(Long volumeId) {
        Volume volume = volumeRepository.findById(volumeId).orElse(null);
        if (volume != null) {
            long count = pageRepository.countByVolumeId(volumeId);
            volume.setTotalPages((int) count);
            volumeRepository.save(volume);
        }
    }

    private boolean isImageFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif") ||
                lower.endsWith(".webp");
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}