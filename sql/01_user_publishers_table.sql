-- Switch Manga - User-Publisher 관계 테이블 추가
-- 이 SQL은 기존 schema.sql에 추가하거나 별도로 실행

USE switchmanga;

-- 1️⃣ User-Publisher 관계 테이블 생성
-- 한 User가 여러 Publisher를 관리할 수 있고, 한 Publisher에 여러 User가 속할 수 있음
CREATE TABLE user_publishers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '관계 ID',
    user_id BIGINT NOT NULL COMMENT '회원 ID',
    publisher_id BIGINT NOT NULL COMMENT '출판사 ID',
    role VARCHAR(20) DEFAULT 'MANAGER' COMMENT '역할 (OWNER, MANAGER, EDITOR)',
    is_primary TINYINT(1) DEFAULT 0 COMMENT '주 출판사 여부',
    active TINYINT(1) DEFAULT 1 COMMENT '활성화 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE CASCADE,
    
    -- 한 User가 같은 Publisher에 중복 등록 방지
    UNIQUE KEY uk_user_publisher (user_id, publisher_id),
    
    INDEX idx_user (user_id),
    INDEX idx_publisher (publisher_id),
    INDEX idx_role (role),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원-출판사 관계';

-- 2️⃣ 샘플 데이터 추가
-- publisher@switchmanga.com (user_id=2) → Marvel Comics (publisher_id=1)에 연결
INSERT INTO user_publishers (user_id, publisher_id, role, is_primary, active) VALUES
(2, 1, 'OWNER', 1, 1);  -- 출판사 계정이 Marvel Comics 소유

-- 3️⃣ 관계 조회 쿼리 예시
-- 특정 User가 관리하는 출판사들
SELECT p.* 
FROM publishers p
JOIN user_publishers up ON p.id = up.publisher_id
WHERE up.user_id = 2 AND up.active = 1;

-- 특정 Publisher를 관리하는 User들
SELECT u.* 
FROM users u
JOIN user_publishers up ON u.id = up.user_id
WHERE up.publisher_id = 1 AND up.active = 1;

-- User의 주 출판사 찾기
SELECT p.* 
FROM publishers p
JOIN user_publishers up ON p.id = up.publisher_id
WHERE up.user_id = 2 AND up.is_primary = 1 AND up.active = 1
LIMIT 1;
