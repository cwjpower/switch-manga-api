# 🚀 PublisherPortalController 적용 가이드

## 📝 파일 위치

**저장 위치:**
```
D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\controller\PublisherPortalController.java
```

---

## ✅ 주요 기능

### 1. Volume 등록
```
POST /api/v1/publishers/me/volumes
```
- JWT 인증
- Publisher 권한 확인
- Series 소유권 검증
- Volume 생성
- 파일 업로드 지원 (coverImage, volumeZip)

### 2. Volume 목록 조회
```
GET /api/v1/publishers/me/volumes
GET /api/v1/publishers/me/volumes?seriesId=1
```

### 3. Volume 상세 조회
```
GET /api/v1/publishers/me/volumes/{id}
```

### 4. Volume 수정
```
PUT /api/v1/publishers/me/volumes/{id}
```

### 5. Volume 삭제 (Soft Delete)
```
DELETE /api/v1/publishers/me/volumes/{id}
```

---

## 🔐 보안 기능

1. **JWT 인증**
   - SecurityContextHolder에서 인증된 사용자 추출
   - User → Publisher 연결 확인

2. **권한 검증**
   - Publisher의 publisher_id 확인
   - Series 소유권 검증
   - 다른 Publisher의 컨텐츠 접근 차단

3. **상세 로깅**
   - 모든 작업에 로그 기록
   - 에러 추적 용이

---

## 🛠️ 설치 방법

### 1단계: 파일 복사

**다운로드한 PublisherPortalController.java를 복사:**
```
D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\controller\
```

### 2단계: 포트 종료

```bash
taskkill /F /IM java.exe
```

### 3단계: 서버 재시작

```bash
cd D:\Again_E-Book\switch-manga-api\switch-manga-api
./gradlew clean bootRun
```

### 4단계: 확인

**서버 로그에서:**
```
Started SwitchMangaApiApplication in X.XXX seconds
Tomcat started on port(s): 8081
```

---

## 📋 필수 Repository 확인

**Controller가 사용하는 Repository:**
```java
- VolumeService ✅
- UserRepository ✅
- PublisherRepository ✅
- SeriesRepository ✅
```

**모두 이미 존재하는지 확인!**

---

## 🧪 Postman 테스트

### 1. 로그인
```
POST http://localhost:8081/api/v1/auth/login

Body (raw-JSON):
{
  "email": "marvel@herocomics.com",
  "password": "test1234"
}
```

**토큰 복사!**

---

### 2. Volume 등록

**URL:**
```
POST http://localhost:8081/api/v1/publishers/me/volumes
```

**Headers:**
```
Authorization: Bearer <토큰>
```

**Body (form-data):**
```
seriesId: 1
volumeNumber: 1
title: 원피스 1권
price: 5000
description: 해적왕을 꿈꾸는 루피의 모험
coverImage: [파일 선택] (선택사항)
volumeZip: [파일 선택] (선택사항)
```

**예상 응답:**
```json
{
  "code": 0,
  "data": {
    "id": 1,
    "seriesId": 1,
    "volumeNumber": 1,
    "title": "원피스 1권",
    "price": 5000,
    "description": "해적왕을 꿈꾸는 루피의 모험",
    "active": true
  },
  "msg": "권이 등록되었습니다"
}
```

---

### 3. Volume 목록 조회

**URL:**
```
GET http://localhost:8081/api/v1/publishers/me/volumes
```

**Headers:**
```
Authorization: Bearer <토큰>
```

**예상 응답:**
```json
{
  "code": 0,
  "data": [
    {
      "id": 1,
      "seriesId": 1,
      "volumeNumber": 1,
      "title": "원피스 1권",
      "price": 5000
    }
  ],
  "msg": "Volume 목록 조회 성공"
}
```

---

## 🐛 트러블슈팅

### 문제 1: "사용자 정보를 가져올 수 없습니다"

**원인:**
- JWT 토큰이 없거나 만료됨
- User의 publisher_id가 null

**해결:**
```sql
-- MariaDB에서 확인
SELECT id, email, role, publisher_id FROM users WHERE email = 'marvel@herocomics.com';

-- publisher_id가 null이면 업데이트
UPDATE users SET publisher_id = 1 WHERE email = 'marvel@herocomics.com';
```

---

### 문제 2: "해당 시리즈에 대한 권한이 없습니다"

**원인:**
- Series의 publisher_id가 다름

**해결:**
```sql
-- Series 확인
SELECT id, title, publisher_id FROM series WHERE id = 1;

-- 올바른 publisher_id로 업데이트
UPDATE series SET publisher_id = 1 WHERE id = 1;
```

---

### 문제 3: 컴파일 에러

**원인:**
- Repository가 없음

**해결:**
```java
// 필요한 Repository 확인
src/main/java/com/switchmanga/api/repository/
- UserRepository.java
- PublisherRepository.java
- SeriesRepository.java
- VolumeRepository.java
```

---

## 📊 로그 확인

**정상 작동 시:**
```
INFO - Volume creation request - seriesId: 1, volumeNumber: 1, title: 원피스 1권
INFO - Attempting to get user with email: marvel@herocomics.com
INFO - User found: marvel@herocomics.com (role: PUBLISHER, publisherId: 1)
INFO - Publisher authenticated: Marvel Comics (ID: 1)
INFO - Volume created successfully: ID 1
```

**에러 발생 시:**
```
WARN - User has no publisher_id: marvel@herocomics.com
ERROR - Cannot get authenticated publisher
```

---

## 🎉 성공 체크리스트

- [ ] PublisherPortalController.java 복사
- [ ] 포트 8081 종료
- [ ] 서버 재시작 성공
- [ ] 로그인 성공 (marvel@herocomics.com)
- [ ] Volume 등록 성공
- [ ] Volume 목록 조회 성공

---

**생성일**: 2025-11-13  
**프로젝트**: Switch Manga API  
**상태**: ✅ Publisher Portal API 완성
