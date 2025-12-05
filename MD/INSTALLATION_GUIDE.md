# 🚀 Publisher Portal Controller 설치 가이드

## 📦 생성된 파일 목록

### 1️⃣ DTO (Data Transfer Objects)
```
✅ SeriesCreateRequest.java    - 시리즈 생성 요청
✅ SeriesUpdateRequest.java    - 시리즈 수정 요청
```

### 2️⃣ Controller
```
✅ PublisherPortalController.java - Publisher Portal API
```

### 3️⃣ Service
```
✅ PublisherService.java - 비즈니스 로직
```

### 4️⃣ Repository
```
✅ SeriesRepository.java - 데이터 접근
```

### 5️⃣ 설정 파일
```
✅ application-multipart.yml - 파일 업로드 설정
```

---

## 📂 파일 배치

### IntelliJ 프로젝트 경로:
```
D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\
```

### 파일 복사 위치:

#### 1. DTO 파일
```
SeriesCreateRequest.java
→ src/main/java/com/switchmanga/api/dto/series/SeriesCreateRequest.java

SeriesUpdateRequest.java
→ src/main/java/com/switchmanga/api/dto/series/SeriesUpdateRequest.java
```

#### 2. Controller 파일
```
PublisherPortalController.java
→ src/main/java/com/switchmanga/api/controller/PublisherPortalController.java
```

#### 3. Service 파일
```
PublisherService.java
→ src/main/java/com/switchmanga/api/service/PublisherService.java

⚠️ 주의: 기존 PublisherService.java가 있다면
- 백업 후 교체 또는
- 메서드만 복사-붙여넣기
```

#### 4. Repository 파일
```
SeriesRepository.java
→ src/main/java/com/switchmanga/api/repository/SeriesRepository.java

⚠️ 주의: 기존 SeriesRepository.java가 있다면
- 백업 후 교체 또는
- 메서드만 추가
```

#### 5. 설정 파일
```
application-multipart.yml 내용을
→ src/main/resources/application.yml 에 추가
```

---

## ⚙️ application.yml 설정 추가

기존 `application.yml` 파일에 다음 내용 추가:

```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB       # 단일 파일 최대 크기
      max-request-size: 10MB    # 전체 요청 최대 크기
      file-size-threshold: 0
```

---

## 📁 업로드 디렉토리 생성

### Linux/Mac (Oracle Cloud):
```bash
sudo mkdir -p /uploads/publishers
sudo chmod 777 /uploads/publishers
```

### Windows (로컬 개발):
```bash
mkdir C:\uploads\publishers
```

또는 Controller 코드에서 경로 수정:
```java
private static final String UPLOAD_DIR = "C:/uploads/publishers/";
```

---

## 🔧 Entity 확인

### Series.java에 필요한 필드:

```java
@Entity
@Table(name = "series")
public class Series {
    // 기본 정보
    private String title;
    private String titleEn;
    private String titleJp;
    private String author;
    private String artist;
    private String description;
    private String status;
    private String coverImage;
    
    // 가격 정보 (추가 필요할 수 있음)
    private String pricingModel;
    private Double defaultPrice;
    private Double rentalPrice;
    private Integer rentalDays;
    private Double subscriptionPrice;
    private Integer freeVolumes;
    
    // 기타
    private String genre;
    private String tags;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 관계
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
}
```

---

## 🚀 빌드 및 실행

### 1. 빌드
```bash
./gradlew clean build
```

### 2. 실행
```bash
./gradlew bootRun
```

### 3. 확인
```
http://152.67.199.56:8081/swagger-ui/index.html
```

---

## 🧪 API 테스트

### Postman/cURL로 테스트:

#### 1️⃣ 시리즈 생성
```bash
curl -X POST "http://152.67.199.56:8081/api/v1/publishers/me/series" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "title=테스트 시리즈" \
  -F "author=테스트 작가" \
  -F "status=ONGOING" \
  -F "pricingModel=PURCHASE" \
  -F "defaultPrice=3000" \
  -F "rentalPrice=1000" \
  -F "rentalDays=7" \
  -F "subscriptionPrice=9900" \
  -F "freeVolumes=3" \
  -F "coverImageFile=@/path/to/image.jpg"
```

#### 2️⃣ 시리즈 목록 조회
```bash
curl -X GET "http://152.67.199.56:8081/api/v1/publishers/me/series?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## ⚠️ 주의사항

### 1. JWT 인증
현재 코드는 JWT 인증을 가정합니다.
- `getCurrentUser()` 메서드가 `SecurityContextHolder`에서 User를 가져옴
- JWT가 없으면 403 Forbidden 에러 발생

### 2. User-Publisher 연결
현재는 임시로 `user.getId()`로 Publisher를 찾습니다.
```java
// PublisherService.java의 getPublisherByUser() 메서드
// TODO: 실제 User-Publisher 연결 테이블 구현 후 수정 필요
```

### 3. 파일 저장 경로
운영 환경에 맞게 수정:
```java
private static final String UPLOAD_DIR = "/uploads/publishers/";
```

---

## 🐛 문제 해결

### 문제 1: 파일 업로드 실패
```
org.apache.tomcat.util.http.fileupload.FileUploadException
```
**해결:** application.yml에 multipart 설정 확인

### 문제 2: 403 Forbidden
```
Access Denied
```
**해결:** JWT 토큰 확인 또는 인증 비활성화 (테스트용)

### 문제 3: 415 Unsupported Media Type
```
415 Error
```
**해결:** 
- Content-Type이 `multipart/form-data`인지 확인
- Postman에서 Body → form-data 선택

### 문제 4: 디렉토리 권한 에러
```
Permission denied
```
**해결:**
```bash
sudo chmod 777 /uploads/publishers
```

---

## ✅ 체크리스트

설치 완료 확인:

- [ ] SeriesCreateRequest.java 복사
- [ ] SeriesUpdateRequest.java 복사
- [ ] PublisherPortalController.java 복사
- [ ] PublisherService.java 복사
- [ ] SeriesRepository.java 복사
- [ ] application.yml 설정 추가
- [ ] /uploads/publishers 디렉토리 생성
- [ ] ./gradlew clean build 성공
- [ ] 서버 실행 성공
- [ ] Swagger UI 접속 확인
- [ ] API 테스트 성공

---

## 📞 문의

문제 발생 시:
1. 빌드 에러 로그 확인
2. Swagger UI에서 API 테스트
3. 로그 파일 확인: `logs/spring-boot-application.log`
4. Claude(찰리)에게 질문! 😊

---

**생성일**: 2025-11-12  
**프로젝트**: Switch Manga API  
**작성자**: Claude (찰리) 💪
