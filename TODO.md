# Switch Manga API & CMS TODO

## ✅ 완료 (2025-10-25)

### Backend API
- [x] Spring Boot 프로젝트 설정
- [x] MariaDB 연결 (Docker, 포트 33060)
- [x] JWT 인증 시스템
- [x] 로그인 API (`/api/v1/auth/login`)
- [x] 비밀번호 해싱 (BCrypt)

### Publisher CMS (거의 완료)
- [x] Dashboard 기능
- [x] 주문 관리 (검색/필터링)
- [x] 매출/수익 추적 (통계, 그래프)
- [x] 시리즈 관리 (상세 정보, 수익 상태)
- [x] 책 업로드 워크플로우 (ZIP 파일 추출/정리)
- [x] 모듈형 UI 구조 (Lego block 아키텍처)
- [x] 퍼플 그라디언트 테마 적용

### 문서화
- [x] WORKFLOW.md (개발 워크플로우)
- [x] TROUBLESHOOTING.md (트러블슈팅)
- [x] BUG_LOG.md (버그 로그)

---

## 🔥 진행 중

### Publisher CMS
- [ ] 로그인 시스템 재구축 (보안 강화)
- [ ] Publisher Portal 기능 완성
  - [ ] 컨텐츠 직접 업로드
  - [ ] 매출 관리 인터페이스
- [ ] Action Viewer 통합 준비

---

## 📋 대기 중 (우선순위 순)

### Phase 1: Publisher Portal API Backend ⭐ 최우선

**1-A. Publisher Authentication & Info**
- [ ] Publisher 회원가입 API
  - [ ] POST /api/v1/auth/register/publisher
  - [ ] 이메일 인증
- [ ] Publisher 프로필 API
  - [ ] GET /api/v1/publishers/me
  - [ ] PUT /api/v1/publishers/me
  - [ ] GET /api/v1/publishers/me/stats
- [ ] 비밀번호 변경/재설정 API

**1-B. Series Management API**
- [ ] GET /api/v1/publishers/me/series (내 시리즈 목록)
- [ ] POST /api/v1/publishers/me/series (시리즈 등록)
- [ ] GET /api/v1/publishers/me/series/{id} (상세)
- [ ] PUT /api/v1/publishers/me/series/{id} (수정)
- [ ] DELETE /api/v1/publishers/me/series/{id} (삭제)
- [ ] GET /api/v1/publishers/me/series/{id}/revenue (시리즈별 매출)

**1-C. Volume Management API**
- [ ] GET /api/v1/publishers/me/volumes (내 권 목록)
- [ ] POST /api/v1/publishers/me/volumes (권 등록)
- [ ] GET /api/v1/publishers/me/volumes/{id} (상세)
- [ ] PUT /api/v1/publishers/me/volumes/{id} (수정)
- [ ] DELETE /api/v1/publishers/me/volumes/{id} (삭제)
- [ ] PATCH /api/v1/publishers/me/volumes/{id}/status (상태 변경)

**1-D. File Upload System**
- [ ] POST /api/v1/upload/image (이미지 업로드)
- [ ] POST /api/v1/upload/zip (ZIP 파일 업로드)
- [ ] ZIP 압축 해제 및 파일 추출
- [ ] 페이지 이미지 처리
- [ ] 썸네일 자동 생성
- [ ] 파일 저장 경로 설정 (/uploads/publishers/{id}/)

**1-E. Revenue & Orders API**
- [ ] GET /api/v1/publishers/me/orders (주문 내역)
- [ ] GET /api/v1/publishers/me/orders/{id} (주문 상세)
- [ ] GET /api/v1/publishers/me/revenue (매출 통계)
- [ ] GET /api/v1/publishers/me/revenue/monthly (월별 매출)
- [ ] GET /api/v1/publishers/me/revenue/detail (상세 매출)
- [ ] 수익 배분 계산 (70% Publisher / 30% Platform)

---

### Phase 2: Super Admin CMS ⭐ 두번째 우선순위

**2-A. Admin Authentication**
- [ ] Admin 로그인 시스템
- [ ] 역할 기반 접근 제어 (RBAC)
- [ ] 권한 관리 (SUPER_ADMIN, ADMIN, MODERATOR)

**2-B. Publisher Management**
- [ ] GET /api/v1/admin/publishers (출판사 전체 목록)
- [ ] GET /api/v1/admin/publishers/{id} (출판사 상세)
- [ ] POST /api/v1/admin/publishers (출판사 등록)
- [ ] PUT /api/v1/admin/publishers/{id} (출판사 수정)
- [ ] PATCH /api/v1/admin/publishers/{id}/status (상태 변경)
- [ ] DELETE /api/v1/admin/publishers/{id} (출판사 삭제)
- [ ] 출판사 승인/거부 시스템

**2-C. Content Management**
- [ ] GET /api/v1/admin/series (전체 시리즈 관리)
- [ ] GET /api/v1/admin/volumes (전체 권 관리)
- [ ] 컨텐츠 승인/거부
- [ ] 컨텐츠 검수 시스템
- [ ] 부적절한 컨텐츠 신고/처리

**2-D. User Management**
- [ ] GET /api/v1/admin/users (전체 사용자 목록)
- [ ] GET /api/v1/admin/users/{id} (사용자 상세)
- [ ] PATCH /api/v1/admin/users/{id}/status (계정 활성화/비활성화)
- [ ] 사용자 구매 내역 조회
- [ ] 환불 처리

**2-E. Platform Statistics**
- [ ] GET /api/v1/admin/stats/overview (전체 통계)
- [ ] GET /api/v1/admin/stats/revenue (플랫폼 매출)
- [ ] GET /api/v1/admin/stats/publishers (출판사별 통계)
- [ ] GET /api/v1/admin/stats/series (인기 시리즈)
- [ ] GET /api/v1/admin/stats/users (사용자 통계)
- [ ] 대시보드 차트 데이터

**2-F. System Settings**
- [ ] 수수료 설정 (현재 30%)
- [ ] 공지사항 관리
- [ ] 배너 관리
- [ ] 약관 관리

---

### Phase 3: Action Viewer Integration

**3-A. AVF File System**
- [ ] AVF 파일 구조 분석
- [ ] AVF 파일 파싱 로직
- [ ] 프레임별 좌표 데이터 저장
- [ ] Frame 엔티티 설계

**3-B. Viewer API**
- [ ] GET /api/v1/viewer/{volumeId}/frames (프레임 목록)
- [ ] GET /api/v1/viewer/{volumeId}/frames/{frameId} (프레임 상세)
- [ ] Action Viewer 메타데이터 API

**3-C. Publisher CMS - AVF Upload**
- [ ] AVF 파일 업로드 인터페이스
- [ ] 프레임 미리보기
- [ ] 프레임 편집/조정

---

### Phase 4: Mobile App Integration

**4-A. User API**
- [ ] 회원가입 API
- [ ] 소셜 로그인 (Google, Apple, Kakao)
- [ ] 프로필 관리

**4-B. Content Discovery**
- [ ] GET /api/v1/series (시리즈 목록)
- [ ] GET /api/v1/series/{id} (시리즈 상세)
- [ ] GET /api/v1/volumes (권 목록)
- [ ] GET /api/v1/volumes/{id} (권 상세)
- [ ] 검색 API
- [ ] 추천 시스템

**4-C. Purchase & Reading**
- [ ] 결제 API (PortOne/IamPort)
- [ ] 구매 내역 API
- [ ] 뷰어 API (페이지별 이미지)
- [ ] 북마크/진행률 API

**4-D. Social Features**
- [ ] 리뷰/평점 시스템
- [ ] 댓글 시스템
- [ ] 좋아요/즐겨찾기

---

### Phase 5: Advanced Features

- [ ] 알림 시스템 (푸시 알림)
- [ ] 검색 엔진 최적화
- [ ] 캐싱 시스템 (Redis)
- [ ] CDN 통합
- [ ] 분석 시스템 (Google Analytics, Mixpanel)
- [ ] 로그 모니터링 (ELK Stack)

---

## 🎯 다음 작업 (이번 주)

### 최우선: Publisher Portal API Backend

**Step 1: Publisher 정보 API (2-3시간)**
- PublisherController 확장
- PublisherService 구현
- PublisherInfoResponse DTO 생성
- GET /api/v1/publishers/me
- PUT /api/v1/publishers/me

**Step 2: 시리즈 관리 API (4-5시간)**
- Series CRUD 구현
- SeriesResponse DTO
- 시리즈 목록/검색 API

**Step 3: 권 관리 API (4-5시간)**
- Volume CRUD 구현
- VolumeResponse DTO
- 권 목록/검색 API

---

## 📊 프로젝트 진행률

**전체:** 15% 완료
- Backend Infrastructure: 30%
- Publisher CMS: 70%
- Super Admin CMS: 0%
- Action Viewer: 0%
- Mobile App: 0%

---

## 🎯 마일스톤

**2025년 12월:** 프로토타입 완성 (Action Viewer 포함) ⭐
**2026년 3월:** 첫 프로덕션 버전

---

## 📝 메모

### Smart Recovery 전략
- 레거시 8년 시스템에서 기능 복원
- 3개 하드코딩 출판사 → 수백 개로 확장
- 기존 디자인 템플릿 60+ 활용
- Action Viewer는 핵심 차별화 기능

### 비즈니스 모델
- 디지털 전용 (종이책 없음)
- 수익 배분: 70% 출판사 / 30% 플랫폼
- 글로벌 일본 만화 유통 목표

---

**최종 업데이트:** 2025-10-25  
**다음 리뷰:** Publisher API 완료 후
