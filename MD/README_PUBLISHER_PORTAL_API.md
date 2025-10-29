# 🎯 Publisher Portal API 구현 가이드

## 📦 생성된 파일 목록

### DTO (Data Transfer Objects)
```
dto/publisher/
├── PublisherInfoResponse.java        - Publisher 정보 응답
├── PublisherUpdateRequest.java       - Publisher 수정 요청
└── PublisherStatsResponse.java       - Publisher 통계 응답

dto/series/
├── SeriesListResponse.java           - Series 목록 응답
└── SeriesCreateRequest.java          - Series 생성 요청

dto/dashboard/
└── DashboardResponse.java            - Dashboard 종합 데이터 응답
```

### Service Layer
```
service/
└── PublisherPortalService.java       - Publisher Portal 비즈니스 로직
```

### Controller
```
controller/
└── PublisherPortalController.java    - Publisher Portal REST API
```

### Repository
```
repository/
├── SeriesRepository.java             - Series 쿼리 메서드
└── VolumeRepository.java             - Volume 쿼리 메서드
```

---

## 📂 파일 적용 방법

### Step 1: DTO 파일 복사
```
IntelliJ 프로젝트 위치:
D:\Again_E-Book\switch-manga-api\switch-manga-api\src\main\java\com\switchmanga\api\

복사할 위치:
dto/publisher/ → com/switchmanga/api/dto/publisher/
dto/series/ → com/switchmanga/api/dto/series/
dto/dashboard/ → com/switchmanga/api/dto/dashboard/
```

### Step 2: Service 파일 복사
```
service/PublisherPortalService.java
→ com/switchmanga/api/service/PublisherPortalService.java
```

### Step 3: Controller 파일 복사
```
controller/PublisherPortalController.java
→ com/switchmanga/api/controller/PublisherPortalController.java
```

### Step 4: Repository 파일 복사 (덮어쓰기 or 병합)
```
repository/SeriesRepository.java
→ com/switchmanga/api/repository/SeriesRepository.java

repository/VolumeRepository.java
→ com/switchmanga/api/repository/VolumeRepository.java
```

**⚠️ 주의:** 
- Repository 파일이 이미 있다면, 메서드만 추가하거나 전체를 교체하세요.
- 기존 메서드가 있다면 충돌이 발생할 수 있습니다.

---

## 🔧 필수 수정 사항

### 1. PublisherPortalService.java 수정

**현재 문제:**
```java
// ❌ getCurrentUser() 메서드가 구현되지 않음
private User getCurrentUser() {
    // ...
    throw new UnsupportedOperationException("User lookup not implemented yet");
}
```

**해결 방법 A - UserRepository 추가 (추천):**
```java
@Service
@RequiredArgsConstructor
public class PublisherPortalService {
    
    private final PublisherRepository publisherRepository;
    private final SeriesRepository seriesRepository;
    private final VolumeRepository volumeRepository;
    private final UserRepository userRepository; // ← 추가!

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }
}
```

**해결 방법 B - 테스트용 임시 코드:**
```java
private User getCurrentUser() {
    // ⚠️ 테스트용 - 첫 번째 ADMIN 사용자 반환
    return userRepository.findAll().stream()
            .filter(u -> u.getRole() == UserRole.ADMIN)
            .findFirst()
            .orElseThrow(() -> new AccessDeniedException("No admin user found"));
}
```

### 2. Entity 필드 확인

**Series Entity에 필요한 필드:**
```java
@Entity
@Table(name = "bt_series")
public class Series {
    // ... 기존 필드들
    
    // ✅ 이 필드들이 있는지 확인!
    private String coverImage;      // 표지 이미지
    private String description;     // 설명
    private String status;          // ONGOING, COMPLETED
    private Boolean active;         // 활성화 여부
    private LocalDateTime createdAt; // 생성일시
}
```

**만약 없다면 추가:**
```java
@Column(length = 255)
private String coverImage;

@Column(length = 1000)
private String description;

@Column(length = 20)
private String status = "ONGOING";

@Column(nullable = false)
private Boolean active = true;

@CreationTimestamp
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;
```

---

## ✅ 빌드 테스트

### 1단계: Import 확인
```bash
IntelliJ에서 자동 import 되는지 확인
빨간 줄이 있으면 Alt+Enter로 import
```

### 2단계: Rebuild
```
Build → Rebuild Project
```

### 3단계: 예상 에러
```
❌ UserRepository를 찾을 수 없습니다
   → PublisherPortalService에 UserRepository 추가

❌ Series에 coverImage 필드가 없습니다
   → Series Entity에 필드 추가

❌ User에 getRole() 메서드가 없습니다
   → User Entity에 role 필드 및 getter 확인
```

---

## 🧪 API 테스트

### 1. 로그인
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@switchmanga.com",
    "password": "test1234"
  }'
```

**응답:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "admin@switchmanga.com",
  "role": "ADMIN"
}
```

### 2. 내 Publisher 정보 조회
```bash
TOKEN="<위에서 받은 토큰>"

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/publishers/me
```

**기대 응답:**
```json
{
  "id": 1,
  "name": "Marvel Comics",
  "logo": "https://...",
  "country": "USA",
  "email": "contact@marvel.com",
  "phone": "123-456-7890",
  "createdAt": "2025-01-01T00:00:00",
  "active": true
}
```

### 3. 통계 조회
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/publishers/me/stats
```

**기대 응답:**
```json
{
  "totalSeries": 15,
  "totalVolumes": 120,
  "totalOrders": 0,
  "totalRevenue": 0.0,
  "monthlyRevenue": 0.0,
  "weeklyRevenue": 0.0,
  "dailyRevenue": 0.0
}
```

### 4. Series 목록 조회
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/publishers/me/series?page=0&size=10"
```

### 5. Series 생성
```bash
curl -X POST http://localhost:8080/api/v1/publishers/me/series \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Series",
    "author": "John Doe",
    "category": "ACTION",
    "description": "An amazing new series",
    "status": "ONGOING",
    "active": true
  }'
```

### 6. Dashboard 조회
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/publishers/me/dashboard
```

---

## 🐛 JWT Issuer 에러 해결

만약 "Jwt issuer is not configured" 에러가 발생하면:

### application.yml 확인
```yaml
jwt:
  secret: your-secret-key-here-make-it-long-and-secure
  expiration: 86400000 # 24 hours
  issuer: switch-manga-api # ← 이 줄 추가!
```

### JwtUtil.java 수정
```java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.issuer:switch-manga-api}") // ← 기본값 추가
    private String issuer;

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuer(issuer) // ← issuer 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
```

---

## 📊 완성된 API 목록

```
✅ GET  /api/v1/publishers/me                  - 내 정보
✅ PUT  /api/v1/publishers/me                  - 내 정보 수정
✅ GET  /api/v1/publishers/me/stats            - 통계
✅ GET  /api/v1/publishers/me/series           - Series 목록
✅ POST /api/v1/publishers/me/series           - Series 생성
✅ DELETE /api/v1/publishers/me/series/{id}    - Series 삭제
✅ GET  /api/v1/publishers/me/dashboard        - Dashboard

⏳ GET  /api/v1/publishers/me/series/{id}      - Series 상세 (TODO)
⏳ PUT  /api/v1/publishers/me/series/{id}      - Series 수정 (TODO)
⏳ GET  /api/v1/publishers/me/volumes          - Volume 목록 (TODO)
⏳ POST /api/v1/publishers/me/volumes          - Volume 생성 (TODO)
⏳ GET  /api/v1/publishers/me/orders           - 주문 내역 (TODO)
⏳ GET  /api/v1/publishers/me/revenue          - 매출 통계 (TODO)
```

---

## 🎯 다음 단계

### 1. Flutter 연동 테스트
```
1. Spring Boot API 실행
2. Flutter 앱 실행
3. 로그인
4. Publisher Portal 화면 확인
5. 실제 데이터 표시 확인!
```

### 2. 나중에 추가할 기능
```
- Series 상세 조회/수정
- Volume CRUD
- 주문 내역 조회
- 매출 통계 (날짜별, Series별)
- 파일 업로드 (이미지, ZIP)
```

---

## 💡 팁

### IntelliJ 단축키
```
Ctrl + Alt + O : Import 정리
Ctrl + Alt + L : 코드 포맷팅
Alt + Enter    : Quick Fix
Ctrl + F9      : 빌드
Shift + F10    : 실행
```

### Git 커밋 전
```bash
# 빌드 테스트
./gradlew clean build

# 실행 테스트
./gradlew bootRun

# 커밋
git add .
git commit -m "feat: Publisher Portal API 구현"
git push origin main
```

---

**작성일**: 2025-10-29  
**프로젝트**: Switch Manga API  
**작성자**: Claude (찰리) 🤖✨
