-- 1. Publisher 먼저 생성
INSERT INTO publishers (id, name, name_en, email, phone, active, created_at, updated_at)
SELECT * FROM (SELECT
                   1 as id,
                   'Test Publisher' as name,
                   'Test Publisher' as name_en,
                   'publisher@test.com' as email,
                   '010-1234-5678' as phone,
                   1 as active,
                   NOW() as created_at,
                   NOW() as updated_at
              ) AS tmp
WHERE NOT EXISTS (
    SELECT id FROM publishers WHERE id = 1
) LIMIT 1;

-- 2. 출판사 계정 생성 (Publisher Portal 테스트용)
INSERT INTO users (id, email, password, username, role, status, publisher_id, created_at, updated_at, email_verified)
SELECT * FROM (SELECT
                   1 as id,
                   'publisher@switchmanga.com' as email,
                   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhLu' as password,
                   'publisher' as username,
                   'PUBLISHER' as role,
                   'ACTIVE' as status,
                   1 as publisher_id,
                   NOW() as created_at,
                   NOW() as updated_at,
                   1 as email_verified
              ) AS tmp
WHERE NOT EXISTS (
    SELECT email FROM users WHERE email = 'publisher@switchmanga.com'
) LIMIT 1;

-- 3. 슈퍼 어드민 계정 (관리자용)
INSERT INTO users (email, password, username, role, status, created_at, updated_at, email_verified)
SELECT * FROM (SELECT
                   'admin@switchmanga.com' as email,
                   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhLu' as password,
                   'admin' as username,
                   'ADMIN' as role,
                   'ACTIVE' as status,
                   NOW() as created_at,
                   NOW() as updated_at,
                   1 as email_verified
              ) AS tmp
WHERE NOT EXISTS (
    SELECT email FROM users WHERE email = 'admin@switchmanga.com'
) LIMIT 1;