USE switchmanga;

CREATE TABLE publishers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '출판사명',
    name_en VARCHAR(100) COMMENT '영문명',
    name_jp VARCHAR(100) COMMENT '일본어명',
    logo VARCHAR(255) COMMENT '로고 이미지 경로',
    country VARCHAR(50) NOT NULL COMMENT '국가 (US, JP, KR)',
    email VARCHAR(100) COMMENT '연락처 이메일',
    phone VARCHAR(20) COMMENT '연락처 전화',
    website VARCHAR(255) COMMENT '웹사이트',
    description TEXT COMMENT '설명',
    active TINYINT(1) DEFAULT 1 COMMENT '활성화 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_country (country),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='출판사';

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL COMMENT '카테고리 코드',
    name_kr VARCHAR(100) COMMENT '한국어명',
    name_en VARCHAR(100) COMMENT '영문명',
    name_jp VARCHAR(100) COMMENT '일본어명',
    parent_id BIGINT COMMENT '부모 카테고리 ID',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    active TINYINT(1) DEFAULT 1 COMMENT '활성화 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent (parent_id),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='카테고리';

CREATE TABLE series (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    publisher_id BIGINT NOT NULL COMMENT '출판사 ID',
    category_id BIGINT COMMENT '카테고리 ID',
    title VARCHAR(200) NOT NULL COMMENT '제목',
    title_en VARCHAR(200) COMMENT '영문 제목',
    title_jp VARCHAR(200) COMMENT '일본어 제목',
    author VARCHAR(100) COMMENT '작가',
    cover_image VARCHAR(255) COMMENT '커버 이미지',
    description TEXT COMMENT '설명',
    status VARCHAR(20) DEFAULT 'ONGOING' COMMENT 'ONGOING, COMPLETED, CANCELLED',
    total_volumes INT DEFAULT 0 COMMENT '총 권수',
    rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '평점 (0.00-5.00)',
    review_count INT DEFAULT 0 COMMENT '리뷰 수',
    view_count INT DEFAULT 0 COMMENT '조회수',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_publisher (publisher_id),
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_rating (rating DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='시리즈';

CREATE TABLE volumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    series_id BIGINT NOT NULL COMMENT '시리즈 ID',
    volume_number INT NOT NULL COMMENT '권 번호',
    title VARCHAR(200) NOT NULL COMMENT '권 제목',
    title_en VARCHAR(200) COMMENT '영문 제목',
    title_jp VARCHAR(200) COMMENT '일본어 제목',
    cover_image VARCHAR(255) COMMENT '커버 이미지',
    description TEXT COMMENT '설명',
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '가격',
    discount_rate INT DEFAULT 0 COMMENT '할인율 (0-100)',
    total_pages INT DEFAULT 0 COMMENT '총 페이지 수',
    file_size BIGINT COMMENT '파일 크기 (bytes)',
    published_date DATE COMMENT '출판일',
    is_free TINYINT(1) DEFAULT 0 COMMENT '무료 여부',
    rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '평점',
    review_count INT DEFAULT 0 COMMENT '리뷰 수',
    purchase_count INT DEFAULT 0 COMMENT '구매 수',
    view_count INT DEFAULT 0 COMMENT '조회수',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE,
    UNIQUE KEY uk_series_volume (series_id, volume_number),
    INDEX idx_series (series_id),
    INDEX idx_price (price),
    INDEX idx_rating (rating DESC),
    INDEX idx_published (published_date DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='권';

CREATE TABLE pages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    volume_id BIGINT NOT NULL COMMENT '권 ID',
    page_number INT NOT NULL COMMENT '페이지 번호',
    image_url VARCHAR(255) NOT NULL COMMENT '이미지 URL',
    thumbnail_url VARCHAR(255) COMMENT '썸네일 URL',
    width INT COMMENT '이미지 가로',
    height INT COMMENT '이미지 세로',
    file_size INT COMMENT '파일 크기 (bytes)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (volume_id) REFERENCES volumes(id) ON DELETE CASCADE,
    UNIQUE KEY uk_volume_page (volume_id, page_number),
    INDEX idx_volume (volume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='페이지';

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '이메일',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (해시)',
    nickname VARCHAR(50) COMMENT '닉네임',
    profile_image VARCHAR(255) COMMENT '프로필 이미지',
    user_level INT DEFAULT 1 COMMENT '1:일반, 7:출판사, 10:관리자',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE, SUSPENDED, DELETED',
    last_login_at TIMESTAMP NULL COMMENT '마지막 로그인',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_user_level (user_level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원';

CREATE TABLE user_wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL COMMENT '회원 ID',
    cash DECIMAL(10,2) DEFAULT 0.00 COMMENT '캐시',
    point DECIMAL(10,2) DEFAULT 0.00 COMMENT '포인트',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원 지갑';

CREATE TABLE cash_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '회원 ID',
    type VARCHAR(20) NOT NULL COMMENT 'CHARGE, USE, REFUND, REWARD',
    amount DECIMAL(10,2) NOT NULL COMMENT '금액',
    balance_after DECIMAL(10,2) NOT NULL COMMENT '거래 후 잔액',
    description VARCHAR(255) COMMENT '설명',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_type (type),
    INDEX idx_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='캐시 로그';

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '회원 ID',
    order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '주문번호',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '총 금액',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '할인 금액',
    final_amount DECIMAL(10,2) NOT NULL COMMENT '최종 결제 금액',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING, COMPLETED, CANCELLED, REFUNDED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status),
    INDEX idx_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문';

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '주문 ID',
    volume_id BIGINT NOT NULL COMMENT '권 ID',
    price DECIMAL(10,2) NOT NULL COMMENT '구매 가격',
    quantity INT DEFAULT 1 COMMENT '수량',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (volume_id) REFERENCES volumes(id) ON DELETE CASCADE,
    INDEX idx_order (order_id),
    INDEX idx_volume (volume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문 상세';

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '주문 ID',
    method VARCHAR(20) NOT NULL COMMENT 'CARD, CASH, POINT, COUPON',
    amount DECIMAL(10,2) NOT NULL COMMENT '결제 금액',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING, SUCCESS, FAILED, CANCELLED',
    pg_transaction_id VARCHAR(100) COMMENT 'PG사 거래 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order (order_id),
    INDEX idx_method (method),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='결제';

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '회원 ID',
    volume_id BIGINT NOT NULL COMMENT '권 ID',
    rating INT NOT NULL COMMENT '평점 (1-5)',
    content TEXT COMMENT '리뷰 내용',
    like_count INT DEFAULT 0 COMMENT '좋아요 수',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (volume_id) REFERENCES volumes(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_volume (volume_id),
    INDEX idx_rating (rating DESC),
    INDEX idx_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='리뷰';

CREATE TABLE review_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL COMMENT '리뷰 ID',
    user_id BIGINT NOT NULL COMMENT '회원 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_review_user (review_id, user_id),
    INDEX idx_review (review_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='리뷰 좋아요';

CREATE TABLE banners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) COMMENT '배너 제목',
    image_url VARCHAR(255) NOT NULL COMMENT '이미지 URL',
    link_url VARCHAR(255) COMMENT '링크 URL',
    link_type VARCHAR(20) COMMENT 'SERIES, VOLUME, EXTERNAL',
    link_id BIGINT COMMENT '링크 대상 ID',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    active TINYINT(1) DEFAULT 1 COMMENT '활성화 여부',
    start_date TIMESTAMP NULL COMMENT '시작일',
    end_date TIMESTAMP NULL COMMENT '종료일',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_active (active),
    INDEX idx_sort (sort_order),
    INDEX idx_dates (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배너';

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL COMMENT 'NOTICE, NEWS, EVENT',
    title VARCHAR(200) NOT NULL COMMENT '제목',
    content TEXT COMMENT '내용',
    thumbnail VARCHAR(255) COMMENT '썸네일 이미지',
    author_id BIGINT COMMENT '작성자 ID',
    view_count INT DEFAULT 0 COMMENT '조회수',
    is_pinned TINYINT(1) DEFAULT 0 COMMENT '상단 고정 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_type (type),
    INDEX idx_pinned (is_pinned),
    INDEX idx_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공지사항/뉴스';

CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL COMMENT '쿠폰 코드',
    name VARCHAR(100) NOT NULL COMMENT '쿠폰 이름',
    discount_type VARCHAR(20) NOT NULL COMMENT 'PERCENT, AMOUNT',
    discount_value DECIMAL(10,2) NOT NULL COMMENT '할인 값',
    min_purchase_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '최소 구매 금액',
    max_discount_amount DECIMAL(10,2) COMMENT '최대 할인 금액',
    total_quantity INT COMMENT '총 발행 수량',
    used_quantity INT DEFAULT 0 COMMENT '사용된 수량',
    start_date TIMESTAMP NULL COMMENT '시작일',
    expire_date TIMESTAMP NULL COMMENT '만료일',
    active TINYINT(1) DEFAULT 1 COMMENT '활성화 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_code (code),
    INDEX idx_active (active),
    INDEX idx_dates (start_date, expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='쿠폰';

CREATE TABLE user_coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '회원 ID',
    coupon_id BIGINT NOT NULL COMMENT '쿠폰 ID',
    used TINYINT(1) DEFAULT 0 COMMENT '사용 여부',
    used_at TIMESTAMP NULL COMMENT '사용일',
    order_id BIGINT COMMENT '사용한 주문 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_coupon (coupon_id),
    INDEX idx_used (used)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원별 쿠폰';

INSERT INTO categories (code, name_kr, name_en, name_jp, parent_id, sort_order) VALUES
-- 대분류
('COMICS', '미국 코믹스', 'American Comics', 'アメコミ', NULL, 1),
('MANGA', '일본 만화', 'Japanese Manga', '日本マンガ', NULL, 2),
('WEBTOON', '한국 웹툰', 'Korean Webtoon', '韓国ウェブトゥーン', NULL, 3),

-- 미국 코믹스 하위
('MARVEL', '마블', 'Marvel', 'マーベル', 1, 1),
('DC', 'DC', 'DC Comics', 'DC', 1, 2),
('IMAGE', '이미지', 'Image Comics', 'イメージ', 1, 3),

-- 일본 만화 하위
('SHONEN', '소년', 'Shonen', '少年', 2, 1),
('SEINEN', '청년', 'Seinen', '青年', 2, 2),
('SHOJO', '소녀', 'Shojo', '少女', 2, 3),
('JOSEI', '여성', 'Josei', '女性', 2, 4);

INSERT INTO publishers (name, name_en, country, website, description) VALUES
-- 미국
('마블 코믹스', 'Marvel Comics', 'US', 'https://www.marvel.com', '스파이더맨, 아이언맨, 어벤져스 등 슈퍼히어로 만화'),
('DC 코믹스', 'DC Comics', 'US', 'https://www.dc.com', '배트맨, 슈퍼맨, 원더우먼 등 슈퍼히어로 만화'),
('이미지 코믹스', 'Image Comics', 'US', 'https://imagecomics.com', '워킹데드, 스폰 등 독립 만화'),

-- 일본
('슈에이샤', 'Shueisha', 'JP', 'https://www.shueisha.co.jp', '주간 소년 점프 발행사'),
('고단샤', 'Kodansha', 'JP', 'https://www.kodansha.co.jp', '주간 소년 매거진 발행사'),
('쇼가쿠칸', 'Shogakukan', 'JP', 'https://www.shogakukan.co.jp', '주간 소년 선데이 발행사');

INSERT INTO series (publisher_id, category_id, title, title_en, author, description, status, total_volumes) VALUES
-- 마블
(1, 4, '스파이더맨', 'Spider-Man', 'Stan Lee', '뉴욕을 지키는 친절한 이웃 히어로', 'ONGOING', 700),
(1, 4, '어벤져스', 'Avengers', 'Stan Lee', '지구 최강의 영웅들', 'ONGOING', 500),

-- DC
(2, 5, '배트맨', 'Batman', 'Bob Kane', '고담시티의 다크 나이트', 'ONGOING', 900),
(2, 5, '슈퍼맨', 'Superman', 'Jerry Siegel', '강철의 사나이', 'ONGOING', 800),

-- 일본 (슈에이샤)
(4, 7, '원피스', 'ONE PIECE', '오다 에이이치로', '해적왕을 꿈꾸는 루피의 모험', 'ONGOING', 107),
(4, 7, '나루토', 'NARUTO', '키시모토 마사시', '닌자 나루토의 성장 이야기', 'COMPLETED', 72),
(4, 7, '블리치', 'BLEACH', '쿠보 타이토', '사신 이치고의 모험', 'COMPLETED', 74);

-- 비밀번호는 'test1234'를 BCrypt 해시한 값 (실제로는 Spring Security에서 처리)
INSERT INTO users (email, password, nickname, user_level, status) VALUES
('admin@switchmanga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '관리자', 10, 'ACTIVE'),
('publisher@switchmanga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '출판사', 7, 'ACTIVE'),
('user@switchmanga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '일반회원', 1, 'ACTIVE');

-- 회원 지갑 생성
INSERT INTO user_wallets (user_id, cash, point) VALUES
(1, 100000.00, 5000.00),
(2, 50000.00, 2000.00),
(3, 10000.00, 1000.00);

USE switchmanga;
SHOW TABLES;

-- 시리즈 검색 성능 테스트
EXPLAIN SELECT * FROM series WHERE publisher_id = 1 AND category_id = 4 ORDER BY created_at DESC LIMIT 20;

-- 권 가격순 정렬 성능 테스트
EXPLAIN SELECT * FROM volumes WHERE series_id = 1 ORDER BY price ASC LIMIT 20;

-- 리뷰 평점순 정렬 성능 테스트
EXPLAIN SELECT * FROM reviews WHERE volume_id = 1 ORDER BY rating DESC LIMIT 10;