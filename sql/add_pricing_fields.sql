-- Series 테이블에 가격 정책 필드 추가
-- Switch Manga - 시리즈 판매 정책 확장

USE switchmanga;

-- 1. 기본 가격 정책
ALTER TABLE series ADD COLUMN default_price DECIMAL(10,2) DEFAULT 3000.00 COMMENT '기본 권 가격' AFTER description;
ALTER TABLE series ADD COLUMN pricing_model VARCHAR(20) DEFAULT 'FIXED' COMMENT '가격 모델: FIXED(고정), RENTAL(대여), SUBSCRIPTION(구독), BUNDLE(묶음)' AFTER default_price;

-- 2. 묶음 판매
ALTER TABLE series ADD COLUMN bundle_price DECIMAL(10,2) DEFAULT NULL COMMENT '전권 묶음 가격' AFTER pricing_model;
ALTER TABLE series ADD COLUMN bundle_discount_rate INT DEFAULT 0 COMMENT '묶음 할인율 (0-100%)' AFTER bundle_price;

-- 3. 대여 정책
ALTER TABLE series ADD COLUMN rental_price DECIMAL(10,2) DEFAULT NULL COMMENT '권당 대여 가격' AFTER bundle_discount_rate;
ALTER TABLE series ADD COLUMN rental_days INT DEFAULT 7 COMMENT '대여 기간 (일)' AFTER rental_price;

-- 4. 구독 정책
ALTER TABLE series ADD COLUMN subscription_price DECIMAL(10,2) DEFAULT NULL COMMENT '구독 가격' AFTER rental_days;
ALTER TABLE series ADD COLUMN subscription_period VARCHAR(20) DEFAULT 'MONTHLY' COMMENT '구독 기간: MONTHLY(월간), YEARLY(연간)' AFTER subscription_price;

-- 5. 무료 제공 정책
ALTER TABLE series ADD COLUMN free_volumes INT DEFAULT 0 COMMENT '무료 제공 권수 (첫 N권)' AFTER subscription_period;

-- 6. 인덱스 추가 (가격 검색 최적화)
ALTER TABLE series ADD INDEX idx_default_price (default_price);
ALTER TABLE series ADD INDEX idx_bundle_price (bundle_price);

-- 확인 쿼리
SELECT 
    id,
    title,
    default_price,
    pricing_model,
    bundle_price,
    bundle_discount_rate,
    rental_price,
    rental_days,
    subscription_price,
    subscription_period,
    free_volumes
FROM series
LIMIT 5;

-- 롤백용 (필요시)
/*
ALTER TABLE series 
    DROP COLUMN default_price,
    DROP COLUMN pricing_model,
    DROP COLUMN bundle_price,
    DROP COLUMN bundle_discount_rate,
    DROP COLUMN rental_price,
    DROP COLUMN rental_days,
    DROP COLUMN subscription_price,
    DROP COLUMN subscription_period,
    DROP COLUMN free_volumes,
    DROP INDEX idx_default_price,
    DROP INDEX idx_bundle_price;
*/
