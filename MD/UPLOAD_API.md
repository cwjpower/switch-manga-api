# Switch Manga Upload API 문서

업로드 관련 API 엔드포인트 문서입니다.

## 📋 목차
- [1. 이미지 업로드](#1-이미지-업로드)
- [2. ZIP 파일 업로드](#2-zip-파일-업로드)
- [3. 통합 Volume 업로드](#3-통합-volume-업로드)
- [4. 테스트 API](#4-테스트-api)

---

## 1. 이미지 업로드

표지 이미지를 업로드합니다.

### Endpoint
```
POST /api/v1/upload/image
```

### Request
**Headers:**
```
Content-Type: multipart/form-data
```

**Body (Form Data):**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| file | File | ✅ | 이미지 파일 (jpg, jpeg, png, gif, webp) |

### Response
**성공 (200 OK):**
```json
{
  "success": true,
  "filePath": "/uploads/books/20251101123456_abc123/cover.jpg",
  "originalFilename": "spiderman-cover.jpg",
  "savedFilename": "cover.jpg",
  "fileSize": 524288,
  "contentType": "image/jpeg",
  "uploadedAt": "2025-11-01T12:34:56.789"
}
```

**실패 (400 Bad Request):**
```json
{
  "success": false,
  "errorMessage": "파일 크기가 너무 큽니다. (최대: 10MB)",
  "uploadedAt": "2025-11-01T12:34:56.789"
}
```

### 제한사항
- 최대 파일 크기: **10MB**
- 허용 형식: **jpg, jpeg, png, gif, webp**
- 저장 위치: `/uploads/books/{timestamp}_{uuid}/cover.{ext}`

### cURL 예제
```bash
curl -X POST http://34.64.84.117:8080/api/v1/upload/image \
  -F "file=@/path/to/cover.jpg"
```

---

## 2. ZIP 파일 업로드

만화 페이지가 담긴 ZIP 파일을 업로드하고 자동으로 압축을 해제합니다.

### Endpoint
```
POST /api/v1/upload/zip
```

### Request
**Headers:**
```
Content-Type: multipart/form-data
```

**Body (Form Data):**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| file | File | ✅ | ZIP 파일 (만화 페이지 이미지들) |

### Response
**성공 (200 OK):**
```json
{
  "success": true,
  "zipFilePath": "/uploads/books/20251101123456_abc123/comic_1730462096123.zip",
  "pagesDirectory": "/uploads/books/20251101123456_abc123/pages/",
  "originalFilename": "spiderman-vol1.zip",
  "zipFileSize": 15728640,
  "extractedFileCount": 25,
  "extractedFiles": [
    "page_001.jpg",
    "page_002.jpg",
    "frame.avf"
  ],
  "hasAvfFile": true,
  "avfFilePath": "/uploads/books/20251101123456_abc123/pages/frame.avf",
  "uploadedAt": "2025-11-01T12:34:56.789"
}
```

**실패 (400 Bad Request):**
```json
{
  "success": false,
  "errorMessage": "파일 크기가 너무 큽니다. (최대: 1024MB)",
  "uploadedAt": "2025-11-01T12:34:56.789"
}
```

### 제한사항
- 최대 파일 크기: **1GB**
- 허용 형식: **zip**
- 자동 압축 해제: `pages/` 폴더에 추출
- AVF 자동 감지: `frame.avf` 파일 재귀 검색

### cURL 예제
```bash
curl -X POST http://34.64.84.117:8080/api/v1/upload/zip \
  -F "file=@/path/to/comic.zip;type=application/zip"
```

---

## 3. 통합 Volume 업로드

표지 이미지, ZIP 파일, Volume 정보를 한번에 업로드하고 DB에 저장합니다.

### Endpoint
```
POST /api/v1/upload/volume
```

### Request
**Headers:**
```
Content-Type: multipart/form-data
```

**Body (Form Data):**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| coverImage | File | ✅ | 표지 이미지 |
| zipFile | File | ❌ | 만화 페이지 ZIP 파일 |
| seriesId | Long | ✅ | 시리즈 ID |
| title | String | ✅ | 제목 |
| titleEn | String | ❌ | 영문 제목 |
| titleJp | String | ❌ | 일본어 제목 |
| author | String | ✅ | 저자 |
| authorEn | String | ❌ | 영문 저자 |
| authorJp | String | ❌ | 일본어 저자 |
| price | Double | ✅ | 가격 |
| discountRate | Integer | ❌ | 할인율 (0-100) |
| volumeNumber | Integer | ✅ | 권수 |
| isbn | String | ❌ | ISBN |
| publishedDate | String | ❌ | 출판일 (YYYY-MM-DD) |
| description | String | ❌ | 설명 |
| descriptionEn | String | ❌ | 영문 설명 |
| descriptionJp | String | ❌ | 일본어 설명 |
| previewPages | Integer | ❌ | 미리보기 페이지 수 |
| ageRating | String | ❌ | 연령 등급 |
| freeTrialDays | Integer | ❌ | 무료 체험 일수 |
| isFree | Boolean | ❌ | 무료 여부 |
| hasAction | Boolean | ❌ | Action Viewer 지원 |

### Response
**성공 (200 OK):**
```json
{
  "success": true,
  "volumeId": 123,
  "title": "Spider-Man Vol. 1",
  "volumeNumber": 1,
  "coverImagePath": "/uploads/books/20251101123456_abc123/cover.jpg",
  "pagesDirectory": "/uploads/books/20251101123456_abc123/pages/",
  "pageCount": 25,
  "hasAvfFile": true,
  "avfFilePath": "/uploads/books/20251101123456_abc123/pages/frame.avf",
  "seriesId": 10,
  "seriesTitle": "Spider-Man",
  "uploadedAt": "2025-11-01T12:34:56.789"
}
```

**실패 (400 Bad Request):**
```json
{
  "success": false,
  "errorMessage": "시리즈를 찾을 수 없습니다: 999",
  "uploadedAt": "2025-11-01T12:34:56.789"
}
```

### cURL 예제
```bash
curl -X POST http://34.64.84.117:8080/api/v1/upload/volume \
  -F "coverImage=@cover.jpg" \
  -F "zipFile=@comic.zip" \
  -F "seriesId=10" \
  -F "title=Spider-Man Vol. 1" \
  -F "author=Stan Lee" \
  -F "price=9.99" \
  -F "volumeNumber=1" \
  -F "publishedDate=2024-01-01"
```

---

## 4. 테스트 API

서버 상태를 확인합니다.

### Endpoint
```
GET /api/v1/upload/test
```

### Response
```
Upload API is ready!
```

### cURL 예제
```bash
curl http://34.64.84.117:8080/api/v1/upload/test
```

---

## 📁 파일 저장 구조
```
/uploads/books/
└── 20251101123456_abc123/     # {YmdHis}_{UUID}
    ├── cover.jpg              # 표지 이미지
    ├── comic_1730462096.zip   # 원본 ZIP (선택)
    └── pages/                 # 압축 해제 폴더
        ├── page_001.jpg
        ├── page_002.jpg
        ├── ...
        └── frame.avf          # AVF 파일 (자동 감지)
```

---

## 🔒 보안

- 현재 인증 없음 (개발 단계)
- 프로덕션 배포 전 JWT 인증 필요
- 파일 업로드 권한 체크 필요

---

## ⚠️ 에러 코드

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 400 | 잘못된 요청 (파일 검증 실패) |
| 401 | 인증 필요 |
| 403 | 권한 없음 |
| 500 | 서버 오류 |

---

## 📞 문의

- 개발자: Claude (찰리)
- 프로젝트: Switch Manga
- 버전: 1.0.0
- 날짜: 2025-11-01