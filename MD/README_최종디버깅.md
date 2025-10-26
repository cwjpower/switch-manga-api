# 🎯 Switch Manga API - 최종 디버깅 가이드

## ✅ 발견한 문제들

### 1️⃣ Publisher Entity - @Builder 어노테이션 없음
```java
// ❌ 현재 (줄 7-8)
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

// ✅ 수정 필요
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ← 이 줄 추가!
public class Publisher {
```

### 2️⃣ PublisherService - User 구조 불일치
```java
// ❌ 잘못된 코드
user.getUserLevel() == 10  // getUserLevel() 메서드 없음!

// ✅ 올바른 코드
user.getRole() == UserRole.ADMIN  // getRole() 사용!
```

### 3️⃣ Repository 메서드명 불일치
```java
// ❌ 잘못된 메서드명
volumeRepository.countBySeriesPublisherId(publisherId);

// ✅ 올바른 메서드명
volumeRepository.countByPublisherId(publisherId);
```

---

## 🔧 수정 방법

### Step 1: Publisher.java 수정 (1줄만 추가!)

**파일 위치:**
```
D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\entity\Publisher.java
```

**수정 내용:**
```java
// 8번째 줄 근처에서
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ← 이 줄 추가!
public class Publisher {
```

**또는:**
- 첨부된 `fixed/Publisher.java` 파일로 **전체 교체**

---

### Step 2: PublisherService.java 교체

**파일 위치:**
```
D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\service\PublisherService.java
```

**방법:**
1. 기존 파일 백업 (이름 변경: `PublisherService.java.backup3`)
2. 첨부된 `fixed/PublisherService.java` 파일로 교체

**주요 변경사항:**
- ✅ `getUserLevel()` → `getRole()` 변경
- ✅ `UserRole.ADMIN` enum 사용
- ✅ `countByPublisherId()` 메서드 사용
- ✅ `user.getPublisher()` 직접 접근

---

### Step 3: SeriesRepository 확인 (선택사항)

**파일 위치:**
```
D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\repository\SeriesRepository.java
```

**확인할 메서드:**
```java
// 이 메서드가 있는지 확인!
Long countByPublisherId(Long publisherId);
```

**없으면 추가:**
```java
@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    
    // 기존 메서드들...
    
    // ✅ 이 메서드 추가!
    Long countByPublisherId(Long publisherId);
}
```

---

## 📊 수정 후 빌드 테스트

### 1단계: Rebuild
```
IntelliJ 메뉴: Build → Rebuild Project
```

### 2단계: 예상되는 결과
```
✅ Publisher.builder() 에러 해결
✅ getUserLevel() 에러 해결
✅ countBySeriesPublisherId() 에러 해결
✅ getCountry() 에러 해결
```

### 3단계: 남을 수 있는 에러
```
⚠️ SeriesRepository.countByPublisherId() 메서드 없음
   → Step 3 참고해서 추가

⚠️ OrderRepository 관련 에러
   → 일단 무시 (try-catch로 처리됨)
```

---

## 🎯 최종 체크리스트

### 필수 수정 (2개)
- [ ] Publisher.java에 `@Builder` 추가
- [ ] PublisherService.java 교체

### 선택 확인 (1개)
- [ ] SeriesRepository에 `countByPublisherId()` 메서드 확인/추가

### 빌드 확인
- [ ] Rebuild 성공
- [ ] 에러 0개 확인
- [ ] 서버 실행: `./gradlew bootRun`

---

## 💡 왜 이런 문제가 생겼나?

### 문제의 원인
1. 우리가 만든 코드: `domain` 패키지 기반
2. 실제 프로젝트: `entity` 패키지 기반
3. User 구조: `userLevel` 필드 vs `role` 필드

### 해결 방법
- ✅ 실제 프로젝트 구조에 맞춰 코드 수정
- ✅ 실제 Entity 필드명 사용
- ✅ 실제 Repository 메서드명 사용

---

## 🚀 다음 단계

### 빌드 성공 후:
1. Postman으로 API 테스트
2. JWT 토큰 발급 테스트
3. Publisher CRUD 테스트

### 나중에 개선할 것:
1. user_publishers 테이블 추가 (여러 출판사 지원)
2. 예외 처리 개선 (Custom Exception)
3. OrderRepository 통계 쿼리 추가

---

**생성일**: 2025-10-25  
**프로젝트**: Switch Manga API  
**상태**: 실제 구조에 맞춰 수정 완료!
