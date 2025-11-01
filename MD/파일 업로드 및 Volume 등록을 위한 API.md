## 📤 Upload API

### 개요
파일 업로드 및 Volume 등록을 위한 API입니다.

### 주요 기능
- ✅ 이미지 업로드 (표지)
- ✅ ZIP 파일 업로드 및 자동 압축 해제
- ✅ AVF 파일 자동 감지 (Action Viewer)
- ✅ 통합 Volume 등록 (파일 + 정보)
- ✅ 트랜잭션 처리 및 롤백

### API 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/upload/test` | 서버 상태 확인 |
| POST | `/api/v1/upload/image` | 이미지 업로드 |
| POST | `/api/v1/upload/zip` | ZIP 파일 업로드 |
| POST | `/api/v1/upload/volume` | 통합 Volume 업로드 |

### 문서
- 📄 [상세 API 문서](./UPLOAD_API.md)
- 📦 [Postman 컬렉션](./postman/)

### 사용 예시

**이미지 업로드:**
```bash
curl -X POST http://34.64.84.117:8080/api/v1/upload/image \
  -F "file=@cover.jpg"
```

**통합 Volume 업로드:**
```bash
curl -X POST http://34.64.84.117:8080/api/v1/upload/volume \
  -F "coverImage=@cover.jpg" \
  -F "zipFile=@comic.zip" \
  -F "seriesId=1" \
  -F "title=Spider-Man Vol. 1" \
  -F "author=Stan Lee" \
  -F "price=9.99" \
  -F "volumeNumber=1"
```

### 파일 저장 구조
```
/uploads/books/
└── {timestamp}_{uuid}/
    ├── cover.jpg           # 표지
    ├── comic_{time}.zip    # 원본 ZIP
    └── pages/              # 압축 해제
        ├── page_001.jpg
        └── frame.avf       # AVF (자동 감지)
```

### Postman 사용법
1. Postman 설치
2. `postman/Switch-Manga-Upload-API.postman_collection.json` Import
3. `postman/Switch-Manga.postman_environment.json` Import
4. Environment 선택 후 테스트