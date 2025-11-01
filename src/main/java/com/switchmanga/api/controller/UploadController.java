package com.switchmanga.api.controller;

import com.switchmanga.api.dto.upload.ImageUploadResponse;
import com.switchmanga.api.dto.upload.ZipUploadResponse;
import com.switchmanga.api.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService fileUploadService;

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
     * 업로드 테스트 (개발용)
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Upload API is ready!");
    }
}