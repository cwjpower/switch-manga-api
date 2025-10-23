package com.switchmanga.api.service;

import com.switchmanga.api.entity.Page;
import com.switchmanga.api.entity.Volume;
import com.switchmanga.api.repository.PageRepository;
import com.switchmanga.api.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageService {

    private final PageRepository pageRepository;
    private final VolumeRepository volumeRepository;

    // 권별 페이지 목록 조회 (페이지 번호 순서대로)
    public List<Page> getPagesByVolume(Long volumeId) {
        return pageRepository.findByVolumeIdOrderByPageNumberAsc(volumeId);
    }

    // 페이지 상세 조회
    public Page getPageById(Long id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("페이지를 찾을 수 없습니다. ID: " + id));
    }

    // 권별 페이지 개수
    public Long getPageCountByVolume(Long volumeId) {
        return pageRepository.countByVolumeId(volumeId);
    }

    // 페이지 생성
    @Transactional
    public Page createPage(Page page, Long volumeId) {
        // Volume 존재 확인
        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ID: " + volumeId));

        // 같은 권에 같은 페이지 번호가 이미 있는지 확인
        pageRepository.findByVolumeIdAndPageNumber(volumeId, page.getPageNumber())
                .ifPresent(p -> {
                    throw new RuntimeException(
                            "이미 존재하는 페이지 번호입니다. Volume: " + volumeId + ", Page: " + page.getPageNumber());
                });

        // Page에 Volume 설정
        page.setVolume(volume);

        return pageRepository.save(page);
    }

    // 페이지 순서 변경
    @Transactional
    public Page updatePageOrder(Long id, Integer newPageNumber) {
        Page page = getPageById(id);

        // 새로운 페이지 번호가 이미 사용 중인지 확인
        pageRepository.findByVolumeIdAndPageNumber(page.getVolume().getId(), newPageNumber)
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        throw new RuntimeException("이미 사용 중인 페이지 번호입니다: " + newPageNumber);
                    }
                });

        page.setPageNumber(newPageNumber);
        return pageRepository.save(page);
    }

    // 페이지 수정 (이미지 URL, AVF 데이터 등)
    @Transactional
    public Page updatePage(Long id, Page pageDetails) {
        Page page = getPageById(id);

        if (pageDetails.getPageNumber() != null) {
            page.setPageNumber(pageDetails.getPageNumber());
        }
        if (pageDetails.getImageUrl() != null) {
            page.setImageUrl(pageDetails.getImageUrl());
        }
        if (pageDetails.getAvfData() != null) {
            page.setAvfData(pageDetails.getAvfData());
        }

        return pageRepository.save(page);
    }

    // 페이지 삭제
    @Transactional
    public void deletePage(Long id) {
        Page page = getPageById(id);
        pageRepository.delete(page);
    }

    // 권별 페이지 전체 삭제
    @Transactional
    public void deleteAllPagesByVolume(Long volumeId) {
        // Volume 존재 확인
        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ID: " + volumeId));

        // 페이지 목록 조회
        List<Page> pages = pageRepository.findByVolumeId(volumeId);

        // 페이지 삭제
        if (!pages.isEmpty()) {
            pageRepository.deleteAll(pages);

            // Volume의 pageCount 업데이트
            volume.setPageCount(0);
            volumeRepository.save(volume);
        }
    }

    // ZIP 파일 업로드 및 페이지 자동 추출
    @Transactional
    public List<Page> uploadZipAndExtractPages(Long volumeId, MultipartFile zipFile) throws IOException {
        // Volume 존재 확인
        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new RuntimeException("Volume을 찾을 수 없습니다. ID: " + volumeId));

        // 업로드 디렉토리 생성
        String uploadDir = "/var/www/html/uploads/volumes/" + volumeId + "/pages/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        List<Page> extractedPages = new ArrayList<>();
        int pageNumber = 1;

        // ZIP 파일 압축 해제
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipFile.getInputStream()))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                // 디렉토리가 아니고 이미지 파일인 경우만 처리
                if (!entry.isDirectory() && isImageFile(entry.getName())) {
                    // 파일 이름 생성
                    String fileName = String.format("page_%03d%s", pageNumber, getFileExtension(entry.getName()));
                    Path filePath = uploadPath.resolve(fileName);

                    // 파일 저장
                    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }

                    // Page 엔티티 생성
                    Page page = new Page();
                    page.setVolume(volume);
                    page.setPageNumber(pageNumber);
                    page.setImageUrl("/uploads/volumes/" + volumeId + "/pages/" + fileName);

                    Page savedPage = pageRepository.save(page);
                    extractedPages.add(savedPage);

                    pageNumber++;
                }
                zis.closeEntry();
            }
        }

        // Volume의 pageCount 업데이트
        volume.setPageCount(extractedPages.size());
        volumeRepository.save(volume);

        return extractedPages;
    }

    // 이미지 파일 확인
    private boolean isImageFile(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".jpg") ||
                lowerFileName.endsWith(".jpeg") ||
                lowerFileName.endsWith(".png") ||
                lowerFileName.endsWith(".webp");
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex > 0) {
            return fileName.substring(lastIndex);
        }
        return ".jpg"; // 기본값
    }
}