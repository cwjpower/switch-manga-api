# 📋 Publisher Portal API 가이드

## 🎯 완성된 파일 목록

### 📂 전체 구조
```
switch-manga-api/src/main/java/com/switchmanga/api/
├── controller/
│   └── PublisherPortalController.java    (1개)
├── service/
│   └── PublisherService.java             (1개)
├── repository/
│   ├── OrderRepository.java              (3개)
│   ├── SeriesRepository.java
│   └── VolumeRepository.java
└── dto/
    ├── series/                            (4개)
    │   ├── SeriesListResponse.java
    │   ├── SeriesCreateRequest.java
    │   ├── SeriesUpdateRequest.java
    │   └── SeriesDetailResponse.java
    └── volume/                            (4개)
        ├── VolumeListResponse.java
        ├── VolumeCreateRequest.java
        ├── VolumeUpdateRequest.java
        └── VolumeDetailResponse.java

총 13개 파일 생성!
```

---

## 🔌 API 엔드포인트 목록

### 1️⃣ Publisher 정보 관리 (3개)

| Method | Endpoint | 설명 | Request | Response |
|--------|----------|------|---------|----------|
| GET | `/api/v1/publishers/me` | 내 출판사 정보 조회 | - | PublisherInfoResponse |
| PUT | `/api/v1/publishers/me` | 내 출판사 정보 수정 | PublisherUpdateRequest | PublisherInfoResponse |
| GET | `/api/v1/publishers/me/stats` | 내 출판사 통계 조회 | - | PublisherStatsResponse |

### 2️⃣ Series 관리 (5개)

| Method | Endpoint | 설명 | Request | Response |
|--------|----------|------|---------|----------|
| GET | `/api/v1/publishers/me/series` | 내 시리즈 목록 조회 | - | List<SeriesListResponse> |
| POST | `/api/v1/publishers/me/series` | 시리즈 생성 | SeriesCreateRequest | SeriesDetailResponse |
| GET | `/api/v1/publishers/me/series/{id}` | 시리즈 상세 조회 | - | SeriesDetailResponse |
| PUT | `/api/v1/publishers/me/series/{id}` | 시리즈 수정 | SeriesUpdateRequest | SeriesDetailResponse |
| DELETE | `/api/v1/publishers/me/series/{id}` | 시리즈 삭제 (Soft) | - | 204 No Content |

### 3️⃣ Volume 관리 (5개)

| Method | Endpoint | 설명 | Request | Response |
|--------|----------|------|---------|----------|
| GET | `/api/v1/publishers/me/volumes` | 내 권 목록 조회 | - | List<VolumeListResponse> |
| POST | `/api/v1/publishers/me/volumes` | 권 생성 | VolumeCreateRequest | VolumeDetailResponse |
| GET | `/api/v1/publishers/me/volumes/{id}` | 권 상세 조회 | - | VolumeDetailResponse |
| PUT | `/api/v1/publishers/me/volumes/{id}` | 권 수정 | VolumeUpdateRequest | VolumeDetailResponse |
| DELETE | `/api/v1/publishers/me/volumes/{id}` | 권 삭제 (Soft) | - | 204 No Content |

**총 13개 API 엔드포인트**

---

## 🔐 인증 방식

모든 API는 JWT 인증이 필요합니다.

### Request Header
```http
Authorization: Bearer <JWT_TOKEN>
```

### 권한
- **PUBLISHER**: 자기 출판사 데이터만 접근 가능
- **ADMIN**: 모든 출판사 데이터 접근 가능

---

## 📝 API 사용 예시

### 1. 내 출판사 정보 조회
```http
GET /api/v1/publishers/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response (200 OK):
{
  "id": 1,
  "name": "Marvel Comics",
  "nameEn": "Marvel Comics",
  "nameJp": "マーベル・コミックス",
  "logo": "https://...",
  "country": "US",
  "email": "contact@marvel.com",
  "phone": "1-800-MARVEL",
  "website": "https://www.marvel.com",
  "description": "Marvel Comics is a publisher...",
  "active": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-10-25T00:00:00"
}
```

### 2. 시리즈 생성
```http
POST /api/v1/publishers/me/series
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

Request Body:
{
  "title": "Spider-Man",
  "titleEn": "Spider-Man",
  "titleJp": "スパイダーマン",
  "description": "The Amazing Spider-Man series...",
  "author": "Stan Lee",
  "illustrator": "Steve Ditko",
  "categoryId": 1,
  "status": "ONGOING",
  "coverImage": "https://..."
}

Response (200 OK):
{
  "id": 101,
  "title": "Spider-Man",
  "titleEn": "Spider-Man",
  "titleJp": "スパイダーマン",
  "description": "The Amazing Spider-Man series...",
  "author": "Stan Lee",
  "illustrator": "Steve Ditko",
  "publisherId": 1,
  "publisherName": "Marvel Comics",
  "categoryId": 1,
  "categoryName": "Super Hero",
  "status": "ONGOING",
  "coverImage": "https://...",
  "volumeCount": 0,
  "viewCount": 0,
  "averageRating": null,
  "reviewCount": 0,
  "active": true,
  "createdAt": "2024-10-25T14:30:00",
  "updatedAt": "2024-10-25T14:30:00"
}
```

### 3. 권 생성
```http
POST /api/v1/publishers/me/volumes
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

Request Body:
{
  "seriesId": 101,
  "volumeNumber": 1,
  "title": "Spider-Man Vol.1",
  "titleEn": "Spider-Man Vol.1",
  "titleJp": "スパイダーマン 第1巻",
  "description": "The first volume...",
  "price": 9.99,
  "isbn": "978-0785190219",
  "publishDate": "2024-10-01",
  "coverImage": "https://...",
  "thumbnail": "https://..."
}

Response (200 OK):
{
  "id": 501,
  "seriesId": 101,
  "seriesTitle": "Spider-Man",
  "publisherId": 1,
  "publisherName": "Marvel Comics",
  "volumeNumber": 1,
  "title": "Spider-Man Vol.1",
  "titleEn": "Spider-Man Vol.1",
  "titleJp": "スパイダーマン 第1巻",
  "description": "The first volume...",
  "price": 9.99,
  "isbn": "978-0785190219",
  "publishDate": "2024-10-01",
  "coverImage": "https://...",
  "thumbnail": "https://...",
  "pageCount": 0,
  "fileSize": null,
  "viewCount": 0,
  "downloadCount": 0,
  "averageRating": null,
  "active": true,
  "createdAt": "2024-10-25T14:35:00",
  "updatedAt": "2024-10-25T14:35:00"
}
```

### 4. 통계 조회
```http
GET /api/v1/publishers/me/stats
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response (200 OK):
{
  "publisherId": 1,
  "publisherName": "Marvel Comics",
  "totalSeries": 25,
  "totalVolumes": 387,
  "totalRevenue": 125430.50,
  "publisherShare": 87801.35,
  "platformFee": 37629.15,
  "totalOrders": 5234,
  "totalOrderItems": 12456,
  "totalReviews": 3456,
  "averageRating": 4.7
}
```

---

## 🔍 에러 응답

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "JWT 토큰이 유효하지 않습니다"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "해당 출판사에 접근 권한이 없습니다"
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "시리즈를 찾을 수 없습니다"
}
```

### 400 Bad Request (Validation)
```json
{
  "error": "Bad Request",
  "message": "입력값이 올바르지 않습니다",
  "errors": {
    "title": "제목은 필수입니다",
    "price": "가격은 0 이상이어야 합니다"
  }
}
```

---

## 📌 주요 특징

### 1️⃣ 권한 검증
- Service Layer에서 자동으로 권한 체크
- PUBLISHER는 자기 데이터만, ADMIN은 전체 접근

### 2️⃣ Soft Delete
- DELETE 요청 시 실제 삭제 X
- `active = false`로 비활성화
- 데이터 복구 가능

### 3️⃣ 다국어 지원
- 모든 제목/이름 필드에 다국어 지원
- `title`, `titleEn`, `titleJp`

### 4️⃣ Validation
- Jakarta Validation 사용
- 요청 데이터 자동 검증

### 5️⃣ 로깅
- 모든 요청 로그 기록
- 디버깅 및 모니터링 용이

---

## 🚀 다음 단계

1. **테스트**: Postman으로 API 테스트
2. **보안**: JWT 토큰 발급/검증 확인
3. **에러 처리**: GlobalExceptionHandler 추가
4. **문서화**: Swagger/OpenAPI 문서 생성
5. **배포**: 서버 배포 및 통합 테스트

---

**작성일**: 2025-10-25
**작성자**: Claude (찰리)
**프로젝트**: Switch Manga API
