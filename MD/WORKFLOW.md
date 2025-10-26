# Switch Manga API 개발 워크플로우

## 🔄 기본 작업 흐름

### IntelliJ (로컬) → GitHub → VM (서버)
```
IntelliJ 수정 → Commit → Push → VM에서 Pull → 빌드 → 테스트
```

---

## 📝 작업 순서

### 1. IntelliJ에서 코드 수정

**수정 후 반드시:**
```bash
# IntelliJ Terminal에서
git status                    # 변경사항 확인
git add .                     # 모든 변경사항 추가
git commit -m "작업 내용"      # 커밋
git push origin main          # GitHub에 푸시
```

**또는 IntelliJ GUI:**
- `Ctrl+K` → 변경사항 확인 → Commit Message 작성 → **Commit and Push**

---

### 2. VM (구글 클라우드)에서 동기화
```bash
cd ~/switch-manga-api

# 1. 현재 변경사항 확인
git status

# 2. 로컬 변경사항 있으면 stash
git stash

# 3. 최신 코드 가져오기
git pull origin main

# 4. 의존성 변경 확인 (build.gradle 수정됐으면)
./gradlew clean build -x test

# 5. 서버 재시작
pkill -9 -f 'java.*switch-manga'
./gradlew bootRun > app.log 2>&1 &

# 6. 로그 확인
tail -f app.log
```

---

## ⚠️ 주의사항

### ❌ 절대 하면 안 되는 것

1. **IntelliJ에서 수정 후 Push 안 하기**
   - VM에서 옛날 코드로 빌드됨!

2. **VM에서 직접 코드 수정하기**
   - IntelliJ와 충돌 발생!
   - 긴급 수정 후 반드시 커밋/푸시!

3. **build.gradle, application.yml 수정 후 확인 안 하기**
   - 의존성 변경은 clean build 필수!

---

## ✅ 체크리스트

### 코드 수정 후

- [ ] IntelliJ에서 빌드 성공 확인
- [ ] Git Commit & Push 완료
- [ ] VM에서 git pull 실행
- [ ] VM에서 빌드 성공 확인
- [ ] API 테스트 완료

### 주요 파일 수정 시

**build.gradle 수정:**
- [ ] IntelliJ: Gradle Sync (Ctrl+Shift+O)
- [ ] VM: `./gradlew clean build -x test`

**application.yml 수정:**
- [ ] IntelliJ: Run → Reload
- [ ] VM: 서버 재시작 필수

**Java 파일 수정:**
- [ ] IntelliJ: Hot Reload 가능
- [ ] VM: 서버 재시작 필요

---

## 🔧 트러블슈팅

### "빌드는 성공했는데 에러가 나요"
```bash
# 1. 코드 동기화 확인
git status
git log --oneline -5

# 2. 특정 파일 확인
git diff HEAD origin/main -- src/main/resources/application.yml

# 3. 강제 동기화
git fetch origin
git reset --hard origin/main
```

### "IntelliJ는 되는데 VM은 안 돼요"
```bash
# 1. 버전 확인
git log -1                    # 현재 커밋
git log origin/main -1        # 서버 최신 커밋

# 2. 다르면
git pull origin main

# 3. build.gradle 비교
diff build.gradle <(curl -s https://raw.githubusercontent.com/cwjpower/switch-manga-api/main/build.gradle)
```

---

## 🚀 빠른 배포 스크립트

### deploy.sh 생성
```bash
nano ~/switch-manga-api/deploy.sh
```

**내용:**
```bash
#!/bin/bash

echo "🚀 Switch Manga API 배포 시작..."

# 1. Git Pull
echo "📥 최신 코드 가져오기..."
git pull origin main

# 2. 빌드
echo "🔨 빌드 중..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패!"
    exit 1
fi

# 3. 기존 프로세스 종료
echo "🛑 기존 서버 종료..."
pkill -9 -f 'java.*switch-manga'

# 4. 서버 시작
echo "▶️  서버 시작..."
./gradlew bootRun > app.log 2>&1 &

# 5. 대기
sleep 15

# 6. 헬스 체크
echo "🏥 헬스 체크..."
curl -s http://localhost:8080/api/v1/auth/validate || echo "⚠️  서버 응답 없음"

echo "✅ 배포 완료!"
echo "📋 로그 확인: tail -f app.log"
```

**실행 권한:**
```bash
chmod +x ~/switch-manga-api/deploy.sh
```

**사용:**
```bash
cd ~/switch-manga-api
./deploy.sh
```

---

## 📊 환경 설정 비교

### IntelliJ (로컬)
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mariadb://localhost:33060/switchmanga
    username: switchmanga
    password: switchmanga123
```

### VM (서버)
```yaml
# application.yml (동일해야 함!)
spring:
  datasource:
    url: jdbc:mariadb://localhost:33060/switchmanga
    username: switchmanga
    password: switchmanga123
```

**DB 접속 정보는 동일!**

---

## 🎯 베스트 프랙티스

1. **매 작업 전: git pull**
2. **매 작업 후: git push**
3. **중요 파일 수정 후: VM 배포 테스트**
4. **에러 발생 시: 코드 동기화부터 확인**

---

## 📞 긴급 상황

### VM에서 급하게 수정했을 때
```bash
# VM에서
git add .
git commit -m "hotfix: 긴급 수정"
git push origin main

# IntelliJ에서
git pull origin main
```

### 충돌 발생 시
```bash
# IntelliJ에서
git stash                     # 로컬 변경사항 임시 저장
git pull origin main          # 서버 코드 가져오기
git stash pop                 # 로컬 변경사항 복원
# 충돌 해결 후 커밋
```

---

## 🔐 중요 파일 목록

**동기화 필수 파일들:**

- `build.gradle` ← 의존성
- `application.yml` ← 설정
- `src/main/java/**/*.java` ← 소스 코드
- `src/main/resources/**` ← 리소스

**동기화 제외:**

- `build/` ← 빌드 결과물
- `app.log` ← 로그 파일
- `.gradle/` ← Gradle 캐시

---

**작성일:** 2025-10-25  
**작성자:** Charlie & 형  
**버전:** 1.0
