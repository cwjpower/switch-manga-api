# 🐛 버그 로그

> 발생한 모든 버그와 해결 방법을 기록합니다.

---

## 📋 템플릿
```
### [날짜] 버그 제목

**🚨 증상:**
- 어떤 문제가 발생했는지

**🔍 원인:**
- 왜 발생했는지

**✅ 해결:**
- 어떻게 해결했는지

**💡 예방:**
- 다시 발생하지 않으려면

**📌 관련 파일:**
- `파일명.java`
- `설정파일.yml`

**🏷️ 태그:** #카테고리 #키워드
```

---

## 2025-10-25 (금)

### [2025-10-25] JWT Claims Immutable 에러

**🚨 증상:**
```json
{"error":"JWT Claims instance is immutable and may not be modified."}
```

**🔍 원인:**
- JwtTokenProvider에서 `Claims` 객체 생성 후 `put()` 메서드로 수정 시도
- JWT 0.12.x 버전부터 Claims가 immutable로 변경됨

**✅ 해결:**
```java
// ❌ 잘못된 코드
Claims claims = Jwts.claims().subject(email).build();
claims.put("role", role);  // 에러!

// ✅ 올바른 코드
return Jwts.builder()
        .subject(email)
        .claim("role", role)  // builder 체이닝 사용
        .issuedAt(now)
        .expiration(validity)
        .signWith(secretKey, Jwts.SIG.HS256)
        .compact();
```

**💡 예방:**
- JWT builder 패턴 사용
- Claims 객체 직접 조작 금지

**📌 관련 파일:**
- `src/main/java/com/switchmanga/api/security/JwtTokenProvider.java`

**🏷️ 태그:** #JWT #Security #API

---

### [2025-10-25] application.yml JWT 설정 누락

**🚨 증상:**
```
Could not resolve placeholder 'jwt.secret' in value "${jwt.secret}"
```

**🔍 원인:**
- application.yml에 JWT 설정이 없음
- @Value 어노테이션이 값을 찾지 못함

**✅ 해결:**
```yaml
# JWT 설정 추가
jwt:
  secret: mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm
  expiration: 86400000  # 24시간
```

**💡 예방:**
- 새 설정 추가 시 application.yml 확인
- IntelliJ와 VM 모두 동기화 확인

**📌 관련 파일:**
- `src/main/resources/application.yml`
- `src/main/java/com/switchmanga/api/security/JwtTokenProvider.java`

**🏷️ 태그:** #Configuration #JWT

---

### [2025-10-25] MariaDB 접근 권한 에러

**🚨 증상:**
```
Access denied for user 'switchmanga'@'172.18.0.1' (using password: YES)
```

**🔍 원인:**
- Spring Boot 앱이 Docker 외부(172.18.0.1)에서 접속
- DB 사용자에게 외부 접속 권한 없음

**✅ 해결:**
```sql
GRANT ALL PRIVILEGES ON switchmanga.* TO 'switchmanga'@'%' IDENTIFIED BY 'switchmanga123';
FLUSH PRIVILEGES;
```

**💡 예방:**
- DB 사용자 생성 시 '@%' 사용 (모든 호스트 허용)
- 또는 특정 IP만 허용: '@172.18.0.%'

**📌 관련 파일:**
- MariaDB 사용자 권한 설정

**🏷️ 태그:** #Database #MariaDB #Permission

---

### [2025-10-25] DB 포트 불일치

**🚨 증상:**
```
Connection refused (localhost:3306)
```

**🔍 원인:**
- Docker MariaDB 포트: `33060:3306`
- application.yml: `jdbc:mariadb://localhost:3306`
- 포트 매핑 불일치

**✅ 해결:**
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:33060/switchmanga  # 33060 사용!
```

**💡 예방:**
- docker-compose.yml 포트 확인: `docker ps`
- application.yml과 일치시키기

**📌 관련 파일:**
- `src/main/resources/application.yml`
- `docker-compose.yml` (MariaDB)

**🏷️ 태그:** #Database #Docker #Port

---

### [2025-10-25] IntelliJ ↔ VM 코드 불일치

**🚨 증상:**
- IntelliJ는 빌드 성공
- VM은 컴파일 에러 (패키지/클래스 없음)

**🔍 원인:**
- IntelliJ에서 수정 후 Git Push 안 함
- VM에서 옛날 코드로 빌드

**✅ 해결:**
```bash
# IntelliJ
git add .
git commit -m "메시지"
git push origin main

# VM
git pull origin main
./gradlew clean build -x test
```

**💡 예방:**
- **작업 후 반드시 Push!**
- **배포 전 반드시 Pull!**
- WORKFLOW.md 체크리스트 사용

**📌 관련 파일:**
- 모든 소스 코드
- `build.gradle`
- `application.yml`

**🏷️ 태그:** #Git #Sync #Workflow

---

### [2025-10-25] Spring Security 의존성 누락

**🚨 증상:**
```
Cannot resolve symbol 'PasswordEncoder'
Cannot resolve symbol 'SecurityFilterChain'
```

**🔍 원인:**
- build.gradle에 spring-boot-starter-security 없음

**✅ 해결:**
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}
```

**💡 예방:**
- Security 기능 사용 전 의존성 확인
- IntelliJ Gradle Sync: Ctrl+Shift+O

**📌 관련 파일:**
- `build.gradle`

**🏷️ 태그:** #Dependency #Security #Gradle

---

## 📊 버그 통계

**카테고리별:**
- 설정 문제: 2건
- 의존성: 1건
- DB 연결: 2건
- Git 동기화: 1건
- JWT: 1건

**가장 많은 원인:**
1. 설정 누락/불일치
2. Git Push/Pull 누락
3. 의존성 누락

---

## 🎯 재발 방지 체크리스트

**새 기능 개발 시:**
- [ ] build.gradle 의존성 추가했나?
- [ ] application.yml 설정 추가했나?
- [ ] IntelliJ에서 빌드 성공했나?
- [ ] Git Push 했나?
- [ ] VM에서 Pull 했나?
- [ ] VM에서 빌드 성공했나?

**DB 관련 작업 시:**
- [ ] 포트 확인했나? (33060)
- [ ] 사용자 권한 확인했나? ('%')
- [ ] 비밀번호 일치하나?

**Security/JWT 작업 시:**
- [ ] 의존성 추가했나?
- [ ] application.yml에 jwt.secret 있나?
- [ ] Claims 직접 수정 안 하고 builder 사용했나?

---

**최종 업데이트:** 2025-10-25  
**총 버그 수:** 7건  
**해결률:** 100%
