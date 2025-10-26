# 트러블슈팅 가이드

## 🔥 오늘 해결한 문제들

### 1. "Could not resolve placeholder 'jwt.secret'"

**원인:** application.yml에 JWT 설정 없음

**해결:**
```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm
  expiration: 86400000
```

---

### 2. "Cannot resolve symbol 'PasswordEncoder'"

**원인:** build.gradle에 Security 의존성 없음

**해결:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
```

---

### 3. "JWT Claims instance is immutable"

**원인:** JwtTokenProvider에서 Claims 객체 수정 시도

**해결:**
```java
// ❌ 잘못된 코드
Claims claims = Jwts.claims().subject(email).build();
claims.put("role", role);  // immutable 에러!

// ✅ 올바른 코드
return Jwts.builder()
        .subject(email)
        .claim("role", role)
        .issuedAt(now)
        .expiration(validity)
        .signWith(secretKey, Jwts.SIG.HS256)
        .compact();
```

---

### 4. "Access denied for user 'switchmanga'@'172.18.0.1'"

**원인:** DB 사용자 권한 없음

**해결:**
```sql
GRANT ALL PRIVILEGES ON switchmanga.* TO 'switchmanga'@'%' IDENTIFIED BY 'switchmanga123';
FLUSH PRIVILEGES;
```

---

### 5. "Connection refused (port 3306)"

**원인:** Docker MariaDB 포트가 33060

**해결:**
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:33060/switchmanga  # 3306 ❌ → 33060 ✅
```

---

### 6. IntelliJ vs VM 코드 불일치

**원인:** Git Push/Pull 안 함

**해결:**
- IntelliJ: 수정 후 반드시 Push
- VM: 배포 전 반드시 Pull

---

**작성일:** 2025-10-25  
**업데이트:** 지속적으로 추가
