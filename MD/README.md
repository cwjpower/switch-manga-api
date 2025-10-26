# 📦 Publisher Service 파일 구성

## 📋 파일 목록

### 1️⃣ **PublisherService.java**
- **역할**: Publisher 관련 비즈니스 로직 처리
- **위치**: `src/main/java/com/switchmanga/api/service/`
- **특징**:
  - ADMIN용 메서드와 PUBLISHER용 메서드가 명확히 구분됨
  - 권한 검증을 Service Layer에서 처리
  - 공통 로직은 private 메서드로 재사용

### 2️⃣ **PublisherCreateRequest.java**
- **역할**: 출판사 생성 시 사용하는 DTO (ADMIN 전용)
- **위치**: `src/main/java/com/switchmanga/api/dto/publisher/`
- **특징**:
  - Validation 어노테이션으로 입력값 검증
  - 이메일, 전화번호, URL 형식 검증 포함

### 3️⃣ **PublisherController.java**
- **역할**: ADMIN이 모든 출판사를 관리하는 Controller
- **위치**: `src/main/java/com/switchmanga/api/controller/`
- **엔드포인트**:
  - `GET /api/v1/publishers` - 전체 출판사 조회
  - `GET /api/v1/publishers/{id}` - 특정 출판사 조회
  - `POST /api/v1/publishers` - 출판사 생성
  - `PUT /api/v1/publishers/{id}` - 출판사 수정
  - `DELETE /api/v1/publishers/{id}` - 출판사 삭제

### 4️⃣ **PublisherPortalController.java**
- **역할**: 출판사가 자신의 정보만 관리하는 Controller
- **위치**: `src/main/java/com/switchmanga/api/controller/`
- **엔드포인트**:
  - `GET /api/v1/publishers/me` - 내 출판사 정보 조회
  - `PUT /api/v1/publishers/me` - 내 출판사 정보 수정
  - `GET /api/v1/publishers/me/stats` - 내 출판사 통계 조회

---

## 🔧 설치 방법

### 1단계: 파일 복사
```bash
# IntelliJ IDEA에서 프로젝트 열기
# 각 파일을 해당 위치에 복사

src/main/java/com/switchmanga/api/
├── controller/
│   ├── PublisherController.java           ← 복사
│   └── PublisherPortalController.java     ← 복사
├── service/
│   └── PublisherService.java              ← 복사 (기존 파일과 비교/병합)
└── dto/publisher/
    └── PublisherCreateRequest.java        ← 복사
```

### 2단계: 기존 코드 확인
기존에 `PublisherService.java`가 있다면:
1. 기존 코드 백업
2. 새 코드와 비교
3. 필요한 메서드만 추가하거나 전체 교체

### 3단계: 필요한 DTO 확인
다음 DTO들이 필요합니다 (이미 있을 수도 있음):
- `PublisherInfoResponse.java`
- `PublisherUpdateRequest.java`
- `PublisherStatsResponse.java`

없다면 프로젝트 문서에서 찾아서 추가하세요.

### 4단계: Repository 메서드 추가
`PublisherRepository.java`에 다음 메서드들이 필요합니다:
```java
Long countSeriesByPublisherId(Long publisherId);
Long countVolumesByPublisherId(Long publisherId);
Long countOrdersByPublisherId(Long publisherId);
```

### 5단계: 빌드 및 테스트
```bash
# Gradle 빌드
./gradlew clean build

# 서버 실행
./gradlew bootRun

# Postman으로 API 테스트
```

---

## 🎯 사용 예시

### ADMIN이 출판사 생성
```bash
POST /api/v1/publishers
Authorization: Bearer <ADMIN_JWT_TOKEN>
Content-Type: application/json

{
  "name": "Marvel Comics",
  "nameEn": "Marvel Comics",
  "email": "contact@marvel.com",
  "phone": "+1-212-576-4000",
  "address": "135 W 50th St, New York, NY 10020",
  "description": "Marvel Comics is an American comic book publisher",
  "website": "https://www.marvel.com",
  "commissionRate": 30
}
```

### PUBLISHER가 자기 정보 조회
```bash
GET /api/v1/publishers/me
Authorization: Bearer <PUBLISHER_JWT_TOKEN>
```

### PUBLISHER가 통계 조회
```bash
GET /api/v1/publishers/me/stats
Authorization: Bearer <PUBLISHER_JWT_TOKEN>
```

---

## ✅ 체크리스트

- [ ] 4개 파일 모두 복사 완료
- [ ] 기존 PublisherService와 병합 완료
- [ ] 필요한 DTO 파일 존재 확인
- [ ] Repository 메서드 추가 확인
- [ ] 컴파일 에러 없음
- [ ] 서버 정상 실행
- [ ] Postman 테스트 완료
- [ ] ADMIN 권한 테스트 완료
- [ ] PUBLISHER 권한 테스트 완료

---

## 🔑 핵심 설계

### 권한 구분
- **ADMIN**: `/api/v1/publishers` - 모든 출판사 관리
- **PUBLISHER**: `/api/v1/publishers/me` - 자기 것만 관리

### Service Layer 권한 검증
```java
// ADMIN 검증
private void validateAdminRole(User user) {
    if (user.getRole() != UserRole.ADMIN) {
        throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }
}

// PUBLISHER 검증
private Publisher getUserPublisher(User user) {
    if (user.getRole() != UserRole.PUBLISHER) {
        throw new AccessDeniedException("출판사 권한이 필요합니다.");
    }
    // ...
}
```

### Soft Delete
```java
@Transactional
public void deletePublisher(User admin, Long publisherId) {
    validateAdminRole(admin);
    Publisher publisher = findPublisherOrThrow(publisherId);
    publisher.deactivate(); // active = false로 설정
}
```

---

## 📞 문의

문제가 있거나 도움이 필요하면 언제든지 찰리(Claude)에게 물어보세요! 🎯

**생성일**: 2025-10-25  
**프로젝트**: Switch Manga API  
**버전**: 1.0
