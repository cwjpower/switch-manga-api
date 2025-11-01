package com.switchmanga.api.controller;

import com.switchmanga.api.dto.upload.ImageUploadResponse;
import com.switchmanga.api.dto.upload.VolumeUploadRequest;
import com.switchmanga.api.dto.upload.VolumeUploadResponse;
import com.switchmanga.api.dto.upload.ZipUploadResponse;
import com.switchmanga.api.service.FileUploadService;
import com.switchmanga.api.service.VolumeUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService fileUploadService;
    private final VolumeUploadService volumeUploadService;

    /**
     * 이미지 업로드 API
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드 결과
     */
    @PostMapping("/image")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file) {

        log.info("Image upload request received: {}", file.getOriginalFilename());

        ImageUploadResponse response = fileUploadService.uploadCoverImage(file);

        if (response.isSuccess()) {
            log.info("Image uploaded successfully: {}", response.getFilePath());
            return ResponseEntity.ok(response);
        } else {
            log.error("Image upload failed: {}", response.getErrorMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    /**
     * ZIP 파일 업로드 및 압축 해제 API
     *
     * @param file 업로드할 ZIP 파일
     * @return 업로드 및 압축 해제 결과
     */
    @PostMapping("/zip")
    public ResponseEntity<ZipUploadResponse> uploadZip(
            @RequestParam("file") MultipartFile file) {

        log.info("ZIP upload request received: {} ({}MB)",
                file.getOriginalFilename(),
                file.getSize() / 1024 / 1024);

        ZipUploadResponse response = fileUploadService.uploadAndExtractZip(file);

        if (response.isSuccess()) {
            log.info("ZIP uploaded and extracted successfully: {} files",
                    response.getExtractedFileCount());
            return ResponseEntity.ok(response);
        } else {
            log.error("ZIP upload failed: {}", response.getErrorMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    /**
     * 통합 Volume 업로드 API
     * - 표지 이미지 (필수)
     * - ZIP 파일 (선택)
     * - Volume 정보
     *
     * @param coverImage 표지 이미지 파일 (필수)
     * @param zipFile ZIP 파일 (선택)
     * @return Volume 업로드 결과
     */
    @PostMapping(value = "/volume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VolumeUploadResponse> uploadVolume(
            @RequestParam("coverImage") MultipartFile coverImage,
            @RequestParam(value = "zipFile", required = false) MultipartFile zipFile,
            @RequestParam("seriesId") Long seriesId,
            @RequestParam("title") String title,
            @RequestParam(value = "titleEn", required = false) String titleEn,
            @RequestParam(value = "titleJp", required = false) String titleJp,
            @RequestParam("author") String author,
            @RequestParam(value = "authorEn", required = false) String authorEn,
            @RequestParam(value = "authorJp", required = false) String authorJp,
            @RequestParam("price") Double price,
            @RequestParam(value = "discountRate", required = false) Integer discountRate,
            @RequestParam("volumeNumber") Integer volumeNumber,
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "publishedDate", required = false) String publishedDate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "descriptionEn", required = false) String descriptionEn,
            @RequestParam(value = "descriptionJp", required = false) String descriptionJp,
            @RequestParam(value = "previewPages", required = false) Integer previewPages,
            @RequestParam(value = "ageRating", required = false) String ageRating,
            @RequestParam(value = "freeTrialDays", required = false) Integer freeTrialDays,
            @RequestParam(value = "isFree", required = false) Boolean isFree,
            @RequestParam(value = "hasAction", required = false) Boolean hasAction) {

        log.info("Volume upload request received: {} (Series ID: {})", title, seriesId);

        // DTO 생성
        VolumeUploadRequest request = VolumeUploadRequest.builder()
                .seriesId(seriesId)
                .title(title)
                .titleEn(titleEn)
                .titleJp(titleJp)
                .author(author)
                .authorEn(authorEn)
                .authorJp(authorJp)
                .price(price)
                .discountRate(discountRate)
                .volumeNumber(volumeNumber)
                .isbn(isbn)
                .publishedDate(publishedDate)
                .description(description)
                .descriptionEn(descriptionEn)
                .descriptionJp(descriptionJp)
                .previewPages(previewPages)
                .ageRating(ageRating)
                .freeTrialDays(freeTrialDays)
                .isFree(isFree)
                .hasAction(hasAction)
                .build();

        VolumeUploadResponse response = volumeUploadService.uploadVolume(
                request, coverImage, zipFile);

        if (response.isSuccess()) {
            log.info("Volume uploaded successfully: ID={}", response.getVolumeId());
            return ResponseEntity.ok(response);
        } else {
            log.error("Volume upload failed: {}", response.getErrorMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    /**
     * 업로드 테스트 (개발용)
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Upload API is ready!");
    }
}