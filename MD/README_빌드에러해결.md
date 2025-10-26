# 🔧 Switch Manga API - 빌드 에러 해결 가이드

## 📊 에러 분석 완료!

### 주요 에러 원인
1. ❌ `com.switchmanga.api.domain.user` 패키지가 없음 → 실제로는 `entity` 패키지
2. ❌ `@AuthUser` 어노테이션이 없음
3. ❌ validation 어노테이션 import 경로 (`javax` → `jakarta`)
4. ❌ User ↔ Publisher 연결 테이블이 없음

---

## 🎯 해결 방법

### Step 1: 새 파일들 추가 (7개)

```
프로젝트 루트: D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\

📁 security/
   ├── AuthUser.java                      ← 새로 추가
   └── AuthUserArgumentResolver.java      ← 새로 추가

📁 config/
   └── WebMvcConfig.java                  ← 새로 추가

📁 controller/
   ├── PublisherController.java           ← 교체
   └── PublisherPortalController.java     ← 교체

📁 dto/publisher/
   └── PublisherCreateRequest.java        ← 교체

📁 service/
   └── PublisherService.java              ← 교체
```

### Step 2: 파일 적용 방법

#### 1️⃣ 새로 추가할 파일들 (3개)
```bash
# IntelliJ에서:
1. security/AuthUser.java
   → src/main/java/com/switchmanga/api/security/ 에 복사

2. security/AuthUserArgumentResolver.java
   → src/main/java/com/switchmanga/api/security/ 에 복사

3. config/WebMvcConfig.java
   → src/main/java/com/switchmanga/api/config/ 에 복사
```

#### 2️⃣ 교체할 파일들 (4개)
```bash
# 기존 파일 백업 후 교체:
1. controller/PublisherController.java
   → 기존 파일을 PublisherController.java.backup2로 이름 변경
   → 새 파일 복사

2. controller/PublisherPortalController.java
   → 기존 파일 덮어쓰기

3. dto/publisher/PublisherCreateRequest.java
   → 기존 파일 덮어쓰기

4. service/PublisherService.java
   → 기존 파일 덮어쓰기
```

---

## ⚠️ 중요 사항

### User-Publisher 연결 문제
현재 DB에 `user_publishers` 테이블이 없어서:
- ✅ **임시 해결**: user_level로 권한만 체크 (빌드는 성공)
- ⚠️ **TODO**: 출판사가 실제로 자기 Publisher에 연결되려면 테이블 추가 필요

### 나중에 해야 할 일
```sql
-- user_publishers 테이블 생성 (01_user_publishers_table.sql 참고)
CREATE TABLE user_publishers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    publisher_id BIGINT NOT NULL,
    role VARCHAR(20) DEFAULT 'MANAGER',
    is_primary TINYINT(1) DEFAULT 0,
    active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_publisher (user_id, publisher_id)
);
```

---

## 🔍 빌드 테스트

### 1단계: Rebuild
```bash
# IntelliJ 메뉴에서:
Build → Rebuild Project
```

### 2단계: 에러 확인
- ✅ `cannot find symbol class User` → 해결됨
- ✅ `package com.switchmanga.api.domain.user does not exist` → 해결됨
- ✅ `cannot find symbol class AuthUser` → 해결됨
- ✅ validation 에러들 → 해결됨

### 3단계: 서버 실행
```bash
./gradlew bootRun
```

---

## 📋 체크리스트

### Phase 1: 빌드 에러 해결 ⭐ (지금!)
- [ ] AuthUser.java 추가
- [ ] AuthUserArgumentResolver.java 추가
- [ ] WebMvcConfig.java 추가
- [ ] PublisherController.java 교체
- [ ] PublisherPortalController.java 교체
- [ ] PublisherCreateRequest.java 교체
- [ ] PublisherService.java 교체
- [ ] Rebuild 성공 확인
- [ ] 서버 실행 확인

### Phase 2: User-Publisher 연결 (나중에)
- [ ] user_publishers 테이블 생성
- [ ] UserPublisher Entity 생성
- [ ] UserPublisherRepository 생성
- [ ] PublisherService에서 실제 연결 구현
- [ ] 테스트

---

## 🚨 문제 발생 시

### 문제 1: validation 에러
```
cannot find symbol class NotBlank, Size, Email...
```
**해결:** build.gradle에 validation 의존성 확인
```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### 문제 2: UserRepository.findByEmail 에러
```
method findByEmail not found in UserRepository
```
**해결:** UserRepository에 메서드 추가
```java
Optional<User> findByEmail(String email);
```

### 문제 3: Repository count 메서드 에러
```
method countByPublisherId not found
```
**해결:** Repository에 메서드 추가 (나중에 같이 처리)

---

## 💡 다음 단계

1. ✅ **Phase 1 완료 후**: 빌드 성공, 서버 실행 확인
2. 📋 **Phase 2 계획**: User-Publisher 연결 구현
3. 🧪 **테스트**: Postman으로 API 테스트

---

**생성일**: 2025-10-25  
**프로젝트**: Switch Manga API  
**상태**: 빌드 에러 해결 중
