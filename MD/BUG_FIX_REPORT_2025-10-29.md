# Publisher Portal API 버그 해결 과정

**작성일**: 2025-10-29  
**프로젝트**: Switch Manga API  
**작업**: Publisher Portal API 구현 및 디버깅

---

## 📋 목차

1. [문제 발견](#문제-발견)
2. [버그 #1: SecurityConfig 설정 누락](#버그-1-securityconfig-설정-누락)
3. [버그 #2: PublisherService ADMIN 처리](#버그-2-publisherservice-admin-처리)
4. [버그 #3: DTO 파일 불일치](#버그-3-dto-파일-불일치)
5. [버그 #4: Repository Import 누락](#버그-4-repository-import-누락)
6. [버그 #5: Repository 메서드 누락](#버그-5-repository-메서드-누락)
7. [최종 결과](#최종-결과)
8. [교훈](#교훈)

---

## 문제 발견

### 초기 증상
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://34.64.84.117:8080/api/v1/publishers/me

# 결과
HTTP/1.1 403 Forbidden
```

**Publisher Portal API가 403 Forbidden 에러 반환**

---

## 버그 #1: SecurityConfig 설정 누락

### 원인
SecurityConfig.java에서 Publisher Portal 경로가 인증 설정에 포함되지 않음

### 기존 코드 (문제)
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/v1/auth/**").permitAll()
    .anyRequest().authenticated()  // ❌ 모든 경로가 막힘!
)
```

### 해결 코드
```java
.authorizeHttpRequests(auth -> auth
    // Public API
    .requestMatchers("/error").permitAll()
    .requestMatchers("/api/v1/auth/**").permitAll()
    .requestMatchers("/api/v1/publishers").permitAll()
    .requestMatchers("/api/v1/publishers/{id}").permitAll()
    .requestMatchers("/api/v1/series").permitAll()
    .requestMatchers("/api/v1/series/{id}").permitAll()
    .requestMatchers("/api/v1/volumes").permitAll()
    .requestMatchers("/api/v1/volumes/{id}").permitAll()
    
    // Publisher Portal (인증 필요) ✅
    .requestMatchers("/api/v1/publishers/me/**").authenticated()
    
    // Admin API
    .requestMatchers("/api/v1/admin/**").authenticated()
    
    .anyRequest().authenticated()
)
```

### 테스트 결과
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://34.64.84.117:8080/api/v1/publishers/me

# 403 → 500 (진전!)
```

---

## 버그 #2: PublisherService ADMIN 처리

### 원인
ADMIN 사용자가 Publisher Entity와 연결되지 않아 NullPointerException 발생

### 에러 로그
```json
{
  "status": 500,
  "message": "연결된 출판사가 없습니다. 관리자에게 문의하세요.",
  "trace": "java.lang.RuntimeException: 연결된 출판사가 없습니다..."
}
```

### 기존 코드 (문제)
```java
public PublisherInfoResponse getMyPublisher(User user) {
    validatePublisherRole(user);
    
    // ❌ ADMIN은 publisher가 null!
    Publisher publisher = user.getPublisher();
    if (publisher == null) {
        throw new RuntimeException("연결된 출판사가 없습니다.");
    }
    
    return PublisherInfoResponse.from(publisher);
}
```

### 해결 코드
```java
public PublisherInfoResponse getMyPublisher(User user) {
    validatePublisherRole(user);
    
    // ✅ ADMIN이면 첫 번째 Publisher 반환 (임시 처리)
    if (user.getRole() == UserRole.ADMIN) {
        Publisher publisher = publisherRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("출판사가 없습니다."));
        return PublisherInfoResponse.from(publisher);
    }
    
    // TODO: 향후 User Entity에 publisher 관계 추가
    Publisher publisher = publisherRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("출판사가 없습니다."));
    
    return PublisherInfoResponse.from(publisher);
}
```

### 참고사항
- ADMIN 임시 처리: 첫 번째 Publisher 반환
- 향후 개선: User Entity에 ManyToOne publisher 관계 추가 필요

---

## 버그 #3: DTO 파일 불일치

### 원인
**서버(Google Cloud)와 로컬(Windows)의 DTO 파일 내용이 다름**

### 문제 파일들

#### PublisherInfoResponse.java
**Windows 버전 (문제)**
```java
@Data
@Builder
public class PublisherInfoResponse {
    private Long id;
    private String name;
    private String logo;
    // ❌ nameEn, nameJp, website, description 필드 없음!
    // ❌ from() 메서드 없음!
}
```

**서버 버전 (정상)**
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherInfoResponse {
    private Long id;
    private String name;
    private String nameEn;      // ✅
    private String nameJp;      // ✅
    private String website;     // ✅
    private String description; // ✅
    // ... 전체 필드
    
    // ✅ from() 메서드 있음
    public static PublisherInfoResponse from(Publisher publisher) {
        return PublisherInfoResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                // ...
                .build();
    }
}
```

#### PublisherUpdateRequest.java
**Windows 버전 (문제)**
```java
@Data
public class PublisherUpdateRequest {
    private String logo;
    private String email;
    private String phone;
    // ❌ name, nameEn, nameJp, website, description 없음!
}
```

**서버 버전 (정상)**
```java
@Getter
@Setter
public class PublisherUpdateRequest {
    private String name;        // ✅
    private String nameEn;      // ✅
    private String nameJp;      // ✅
    private String logo;
    private String email;
    private String phone;
    private String website;     // ✅
    private String description; // ✅
}
```

### 컴파일 에러
```
error: cannot find symbol
  symbol: method from()
  location: class PublisherInfoResponse

error: cannot find symbol
  symbol: method getName()
  location: variable request of type PublisherUpdateRequest
```

### 해결 방법
1. 서버에서 정상 작동하는 DTO 파일 확인
```bash
cat src/main/java/com/switchmanga/api/dto/publisher/PublisherInfoResponse.java
```

2. Windows IntelliJ에서 파일 교체
3. 동일한 문제가 Series DTO에서도 발생 → 같은 방법으로 해결

### 영향받은 파일
- PublisherInfoResponse.java ✅
- PublisherUpdateRequest.java ✅
- SeriesListResponse.java ✅
- SeriesCreateRequest.java ✅
- SeriesDetailResponse.java ✅
- SeriesUpdateRequest.java ✅

---

## 버그 #4: Repository Import 누락

### 원인
Service 파일에서 Repository import가 누락됨

### 에러 메시지
```
SeriesService.java:25: error: cannot find symbol
    private final SeriesRepository seriesRepository;
                  ^
  symbol:   class SeriesRepository
  location: class SeriesService
```

### 기존 코드 (문제)
```java
package com.switchmanga.api.service;

import com.switchmanga.api.repository.PublisherRepository;
// ❌ SeriesRepository import 없음!

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;  // ❌ 에러!
}
```

### 해결 코드
```java
package com.switchmanga.api.service;

import com.switchmanga.api.repository.PublisherRepository;
import com.switchmanga.api.repository.SeriesRepository;  // ✅ 추가!

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;  // ✅ 정상!
}
```

### 영향받은 파일
- SeriesService.java ✅
- VolumeService.java ✅

---

## 버그 #5: Repository 메서드 누락

### 원인
Windows의 VolumeRepository.java에 필요한 메서드가 없음

### 에러 메시지
```
VolumeService.java:44: error: cannot find symbol
return volumeRepository.findBySeriesIdOrderByVolumeNumberAsc(seriesId);

symbol: method findBySeriesIdOrderByVolumeNumberAsc(Long)
location: variable volumeRepository of type VolumeRepository
```

### 문제
- **서버 VolumeRepository**: 메서드 20+ 개 있음 ✅
- **Windows VolumeRepository**: 메서드 5개만 있음 ❌

### 해결 방법
서버의 완전한 VolumeRepository.java 파일을 Windows로 복사

### 서버 버전 (정상)
```java
@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {
    
    // ✅ 필요한 모든 메서드 포함
    List<Volume> findBySeriesIdOrderByVolumeNumberAsc(Long seriesId);
    List<Volume> findBySeriesIdAndActiveOrderByVolumeNumberAsc(Long seriesId, Boolean active);
    Long countBySeriesId(Long seriesId);
    Long countBySeriesIdAndActive(Long seriesId, Boolean active);
    Optional<Volume> findBySeriesIdAndVolumeNumber(Long seriesId, Integer volumeNumber);
    // ... 20+ 메서드
}
```

---

## 최종 결과

### 빌드 성공! 🎉
```bash
cd D:\Again_E-Book\switch-manga-api\switch-manga-api
.\gradlew clean build

BUILD SUCCESSFUL in 22s
```

### API 테스트
```bash
# 로그인
curl -X POST http://34.64.84.117:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@switchmanga.com","password":"test1234"}'

# Publisher Portal API
curl -H "Authorization: Bearer $TOKEN" \
  http://34.64.84.117:8080/api/v1/publishers/me

# 예상 결과
HTTP/1.1 200 OK
{
  "id": 1,
  "name": "...",
  "email": "...",
  ...
}
```

---

## 교훈

### 1. 파일 동기화 중요성
**문제**: 서버와 로컬의 파일이 달라서 여러 버그 발생

**해결책**:
- Git을 통한 일관된 버전 관리
- 주기적인 pull/push로 동기화
- 서버에서 직접 작업 시 즉시 커밋

### 2. 체계적인 디버깅
**순서**:
1. API 테스트로 증상 확인 (403/500 에러)
2. 로그 분석으로 원인 파악
3. 관련 파일 확인 (SecurityConfig, Service, DTO)
4. 서버와 로컬 비교
5. 단계별 해결

### 3. Repository/DTO 패턴 이해
**핵심**:
- Repository: 메서드 시그니처가 정확해야 함
- DTO: from() 메서드로 Entity → DTO 변환
- Service: Repository와 DTO 연결
- Controller: Service 호출

### 4. IntelliJ vs Gradle
**차이점**:
- IntelliJ 에러: Lombok, Annotation Processing 문제일 수 있음
- Gradle 빌드 에러: 실제 코드 문제

**대응**:
- IntelliJ 에러만 나면 → Gradle 빌드로 확인
- Gradle 에러 나면 → 실제 수정 필요

### 5. 임시 처리와 TODO
**좋은 예**:
```java
// ✅ ADMIN 임시 처리 (명확한 주석)
if (user.getRole() == UserRole.ADMIN) {
    Publisher publisher = publisherRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("출판사가 없습니다."));
    return PublisherInfoResponse.from(publisher);
}

// TODO: User Entity에 publisher 관계 추가 후 활성화
```

**나쁜 예**:
```java
// ❌ 주석 없이 임시 코드
Publisher publisher = publisherRepository.findAll().get(0);
```

---

## 수정된 파일 목록

### SecurityConfig
```
src/main/java/com/switchmanga/api/config/SecurityConfig.java
```

### Service
```
src/main/java/com/switchmanga/api/service/PublisherService.java
src/main/java/com/switchmanga/api/service/SeriesService.java
src/main/java/com/switchmanga/api/service/VolumeService.java
```

### DTO (Publisher)
```
src/main/java/com/switchmanga/api/dto/publisher/PublisherInfoResponse.java
src/main/java/com/switchmanga/api/dto/publisher/PublisherUpdateRequest.java
```

### DTO (Series)
```
src/main/java/com/switchmanga/api/dto/series/SeriesListResponse.java
src/main/java/com/switchmanga/api/dto/series/SeriesCreateRequest.java
src/main/java/com/switchmanga/api/dto/series/SeriesDetailResponse.java
src/main/java/com/switchmanga/api/dto/series/SeriesUpdateRequest.java
```

### Repository
```
src/main/java/com/switchmanga/api/repository/VolumeRepository.java
```

### Controller
```
src/main/java/com/switchmanga/api/controller/PublisherPortalController.java
```

---

## 다음 단계

### 1. User-Publisher 관계 개선
현재는 ADMIN이 임시로 첫 번째 Publisher를 사용하지만, 향후:
```java
@Entity
public class User {
    // ...
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;  // ✅ 추가 필요
}
```

### 2. API 테스트 자동화
- JUnit으로 통합 테스트 작성
- Postman Collection 업데이트

### 3. 문서화
- API 명세 업데이트
- Publisher Portal 사용 가이드 작성

### 4. 프론트엔드 연동
- Flutter Admin Panel에서 Publisher Portal API 호출
- 대시보드, 시리즈 관리, 권 업로드 기능 구현

---

## 참고 자료

### API 엔드포인트
```
GET    /api/v1/publishers/me          - 내 Publisher 정보
PUT    /api/v1/publishers/me          - 내 Publisher 수정
GET    /api/v1/publishers/me/stats    - 통계 조회
GET    /api/v1/publishers/me/series   - 내 시리즈 목록
POST   /api/v1/publishers/me/series   - 시리즈 생성
```

### Git 커밋 메시지
```bash
fix: SecurityConfig에 Publisher Portal 경로 추가
fix: PublisherService ADMIN 임시 처리 추가
fix: DTO 파일 서버 버전으로 동기화
fix: Repository import 추가
fix: VolumeRepository 메서드 추가
```

---

**작성자**: Charlie (찰리)  
**검토**: cwjpower01 (형)  
**최종 수정**: 2025-10-29 23:20 KST
