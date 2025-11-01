# 403 Forbidden → 404 Not Found 해결 과정 (2025-11-01)

## 📋 문제 요약

Spring Boot API에서 `/test` 엔드포인트 접근 시:
1. 처음: `403 Forbidden` 에러
2. 수정 후: `404 Not Found` 에러
3. 최종: `200 OK` 성공

---

## 🔍 문제 발생 과정

### 1단계: 403 Forbidden

**증상:**
```bash
curl http://localhost:8080/test
# {"timestamp":"2025-11-01T00:07:28.950+00:00","status":403,"error":"Forbidden","path":"/test"}
```

**원인:**
- JwtAuthenticationFilter에 `/test` 경로가 `EXCLUDED_PATHS`에 없음
- SecurityConfig에도 `/test` 경로가 `permitAll()` 목록에 없음
- JWT 인증 필터가 토큰이 없는 요청을 차단

**시도한 해결책:**
```java
// JwtAuthenticationFilter.java
private static final List<String> EXCLUDED_PATHS = List.of(
    "/test",  // ⭐ 추가
    "/api/v1/auth/",
    // ...
);
```

**결과:** 여전히 403 발생!

---

### 2단계: 빌드 캐시 문제

**증상:**
```bash
./gradlew build -x test
# BUILD SUCCESSFUL in 18s
# 5 actionable tasks: 5 up-to-date  ← 문제!
```

**원인:**
- `nano`로 직접 파일 수정
- Gradle이 변경사항을 감지하지 못함
- 이전 빌드 결과(캐시) 사용

**해결:**
```bash
./gradlew clean build -x test  # clean 추가!
```

**교훈:** 파일 직접 수정 시 항상 `clean` 사용!

---

### 3단계: SecurityConfig 누락

**증상:**
- JwtAuthenticationFilter 수정 완료
- Clean 빌드 완료
- 여전히 403 발생!

**원인 분석:**
```java
// SecurityConfig.java
.requestMatchers("/api/v1/auth/**").permitAll()
.requestMatchers("/api/v1/publishers").permitAll()
// ❌ /test가 없음!

.anyRequest().authenticated()  // ← /test가 여기로 가서 인증 필요!
```

**핵심:** 
- JwtAuthenticationFilter의 `EXCLUDED_PATHS`는 JWT 토큰 검증만 건너뜀
- SecurityFilterChain의 `authorizeHttpRequests`가 최종 권한 검사
- **둘 다 설정해야 함!**

**해결:**
```java
// SecurityConfig.java
.requestMatchers("/error").permitAll()
.requestMatchers("/test").permitAll()  // ⭐ 추가
.requestMatchers("/api/v1/auth/**").permitAll()
```

---

### 4단계: 404 Not Found (진전!)

**증상:**
```bash
curl -i http://localhost:8080/test
# HTTP/1.1 404 
# {"timestamp":"2025-11-01T01:17:16.913+00:00","status":404,"error":"Not Found","path":"/test"}
```

**원인:**
- 403 → 404로 변경 = **보안 통과 성공!**
- `/test` 컨트롤러가 없음

**해결:**
```java
// TestController.java
@RestController
public class TestController {
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Server is running!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
```

---

### 5단계: 200 OK 성공! 🎉

**결과:**
```bash
curl http://localhost:8080/test
# {"message":"Server is running!","status":"success","timestamp":1761960432302}
```

---

## 🎯 핵심 교훈

### 1. Spring Security 동작 순서
```
Request → JwtAuthenticationFilter → SecurityFilterChain → Controller
```

### 2. 인증 없이 접근 허용하려면
**모두 설정 필요:**

1. **JwtAuthenticationFilter.java**
```java
private static final List<String> EXCLUDED_PATHS = List.of(
    "/test",  // JWT 토큰 검증 건너뛰기
    // ...
);
```

2. **SecurityConfig.java**
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/test").permitAll()  // 인증 없이 허용
    // ...
);
```

### 3. 빌드 관련
- 파일 직접 수정 시: `./gradlew clean build`
- IntelliJ 수정 시: 일반 빌드 OK
- Gradle 캐시 의심되면: 항상 `clean` 추가

### 4. 에러 코드 의미
- **403 Forbidden**: 보안 필터에서 차단 (인증/권한 문제)
- **404 Not Found**: 보안 통과, 컨트롤러 없음
- **401 Unauthorized**: 인증 필요 (토큰 만료 등)

---

## 📊 문제 해결 체크리스트

공개 API 엔드포인트 추가 시:

- [ ] JwtAuthenticationFilter의 `EXCLUDED_PATHS`에 추가
- [ ] SecurityConfig의 `requestMatchers().permitAll()` 추가
- [ ] Controller 생성
- [ ] `./gradlew clean build` 실행
- [ ] 서버 재시작
- [ ] 테스트: `curl -i http://localhost:8080/endpoint`

---

## 🔐 보안 설정 패턴

### Public API (인증 불필요)
```java
// SecurityConfig.java
.requestMatchers("/api/v1/auth/**").permitAll()
.requestMatchers("/test").permitAll()
.requestMatchers("/actuator/health").permitAll()
```

### Protected API (인증 필요)
```java
.requestMatchers("/api/v1/publishers/me/**").authenticated()
.requestMatchers("/api/v1/admin/**").authenticated()
```

### JWT Filter 제외 경로
```java
// JwtAuthenticationFilter.java
private static final List<String> EXCLUDED_PATHS = List.of(
    "/test",
    "/api/v1/auth/",      // 로그인/회원가입
    "/swagger-ui/",       // API 문서
    "/v3/api-docs/",
    "/api/v1/publishers", // 공개 조회
    "/api/v1/series",
    "/api/v1/volumes"
);
```

---

## 📌 관련 파일

- `src/main/java/com/switchmanga/api/config/SecurityConfig.java`
- `src/main/java/com/switchmanga/api/security/JwtAuthenticationFilter.java`
- `src/main/java/com/switchmanga/api/controller/TestController.java`
- `.gitignore` (app.log 추가)

---

## 🚀 Git 작업

### 발생한 문제들:
1. **100MB 파일 제한**: backup/ 폴더가 Git에 포함됨
2. **비밀번호 인증 차단**: GitHub PAT 또는 SSH 필요
3. **Merge 충돌**: IntelliJ ↔ GCP 버전 차이

### 해결:
```bash
# SSH 키 설정
ssh-keygen -t ed25519 -C "cwjpower01@gmail.com"
git remote set-url origin git@github.com:cwjpower/switch-manga-api.git

# .gitignore 추가
echo "app.log" >> .gitignore
echo "backup/" >> .gitignore

# Merge 충돌 해결
git checkout --ours <file>  # 현재 브랜치 버전 사용
git add <file>
git rebase --continue
```

---

## 💡 개선 사항

### 1. 공통 상수 관리
```java
// SecurityConstants.java
public class SecurityConstants {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/test",
        "/api/v1/auth/**",
        "/swagger-ui/**"
    );
}
```

### 2. Custom AuthenticationEntryPoint
```java
@Bean
public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, authException) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" 
            + authException.getMessage() + "\"}");
    };
}
```

### 3. 통합 테스트
```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Test
    void testPublicEndpoint() {
        mockMvc.perform(get("/test"))
            .andExpect(status().isOk());
    }
}
```

---

**작성일:** 2025-11-01  
**작성자:** Charlie & 형  
**키워드:** #403 #404 #SpringSecurity #JWT #Troubleshooting  
**해결 시간:** 약 1시간