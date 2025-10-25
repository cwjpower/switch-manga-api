-- =====================================================
-- Switch Manga 샘플 데이터
-- 일본 망가를 전 세계에 배급하는 디지털 플랫폼
-- =====================================================

-- 기존 샘플 데이터 삭제 (있다면)
DELETE FROM bt_order_items WHERE 1=1;
DELETE FROM bt_orders WHERE 1=1;
DELETE FROM bt_reviews WHERE 1=1;
DELETE FROM bt_pages WHERE 1=1;
DELETE FROM bt_volumes WHERE 1=1;
DELETE FROM bt_series WHERE 1=1;
DELETE FROM bt_users WHERE user_id > 0;
DELETE FROM bt_publishers WHERE publisher_id > 0;

-- =====================================================
-- 1. 일본 3대 출판사
-- =====================================================

INSERT INTO bt_publishers (publisher_id, name, description, logo_url, website_url, contact_email, contact_phone, is_active, created_at, updated_at) VALUES
(1, 'Shueisha', '주간 소년 점프를 발행하는 일본 최대 망가 출판사. 원피스, 나루토, 드래곤볼 등 전설적인 작품들을 출간.', 
 'https://via.placeholder.com/300x150?text=Shueisha', 'https://www.shueisha.co.jp', 'contact@shueisha.co.jp', '+81-3-3230-6111', true, NOW(), NOW()),

(2, 'Kodansha', '주간 소년 매거진을 발행하는 일본 최대 출판사 중 하나. 진격의 거인, 페어리테일 등의 인기작 보유.', 
 'https://via.placeholder.com/300x150?text=Kodansha', 'https://www.kodansha.co.jp', 'contact@kodansha.co.jp', '+81-3-5395-3500', true, NOW(), NOW()),

(3, 'Shogakukan', '주간 소년 선데이를 발행하는 일본 대형 출판사. 명탐정 코난, 이누야샤 등의 장기 연재작 보유.', 
 'https://via.placeholder.com/300x150?text=Shogakukan', 'https://www.shogakukan.co.jp', 'contact@shogakukan.co.jp', '+81-3-3230-5555', true, NOW(), NOW());

-- =====================================================
-- 2. 사용자 계정
-- =====================================================

INSERT INTO bt_users (user_id, email, username, password, phone, profile_image, birth_date, gender, role, status, email_verified, last_login_at, created_at, updated_at) VALUES
-- 관리자
(1, 'admin@switchmanga.com', 'admin', '$2a$10$rOWqUXz1qXGq9H.EYvJxXOYwUxGPZfJxN7N8t0pL/8.LZXZqXY1Yi', '+1-555-0100', NULL, '1990-01-01', 'OTHER', 'ADMIN', 'ACTIVE', true, NOW(), NOW(), NOW()),

-- 출판사 계정
(2, 'publisher@shueisha.co.jp', 'shueisha_admin', '$2a$10$rOWqUXz1qXGq9H.EYvJxXOYwUxGPZfJxN7N8t0pL/8.LZXZqXY1Yi', '+81-3-3230-6111', NULL, '1985-06-15', 'MALE', 'PUBLISHER', 'ACTIVE', true, NOW(), NOW(), NOW()),

(3, 'publisher@kodansha.co.jp', 'kodansha_admin', '$2a$10$rOWqUXz1qXGq9H.EYvJxXOYwUxGPZfJxN7N8t0pL/8.LZXZqXY1Yi', '+81-3-5395-3500', NULL, '1988-03-20', 'FEMALE', 'PUBLISHER', 'ACTIVE', true, NOW(), NOW(), NOW()),

-- 일반 사용자 (글로벌 망가 팬)
(4, 'john.smith@email.com', 'manga_lover_us', '$2a$10$rOWqUXz1qXGq9H.EYvJxXOYwUxGPZfJxN7N8t0pL/8.LZXZqXY1Yi', '+1-555-0201', NULL, '1995-08-12', 'MALE', 'USER', 'ACTIVE', true, NOW(), NOW(), NOW()),

(5, 'sakura.tanaka@email.com', 'sakura_jp', '$2a$10$rOWqUXz1qXGq9H.EYvJxXOYwUxGPZfJxN7N8t0pL/8.LZXZqXY1Yi', '+81-90-1234-5678', NULL, '1998-04-05', 'FEMALE', 'USER', 'ACTIVE', true, NOW(), NOW(), NOW()),

(6, 'kim.minho@email.com', 'minho_kr', '$2a$10$rOWqUXz1qXGq9H.EYvJxXOYwUxGPZfJxN7N8t0pL/8.LZXZqXY1Yi', '+82-10-1234-5678', NULL, '1997-11-20', 'MALE', 'USER', 'ACTIVE', true, NOW(), NOW(), NOW());

-- =====================================================
-- 3. 전설적인 망가 시리즈
-- =====================================================

INSERT INTO bt_series (series_id, publisher_id, title, description, author, artist, genre, status, cover_image_url, total_volumes, is_active, created_at, updated_at) VALUES
-- Shueisha 작품
(1, 1, 'One Piece', 
 '해적왕을 꿈꾸는 소년 루피와 동료들의 모험 이야기. 1997년부터 연재 중인 전설적인 장편 망가. 전 세계적으로 5억 부 이상 판매된 최고의 베스트셀러.',
 'Eiichiro Oda', 'Eiichiro Oda', 'Adventure, Fantasy, Comedy', 'ONGOING', 
 'https://via.placeholder.com/400x600?text=One+Piece', 105, true, NOW(), NOW()),

(2, 1, 'Naruto', 
 '닌자 세계를 배경으로 한 소년 나루토의 성장과 모험. 1999년부터 2014년까지 연재되어 전 세계 망가 팬들의 사랑을 받은 명작.',
 'Masashi Kishimoto', 'Masashi Kishimoto', 'Action, Adventure, Fantasy', 'COMPLETED', 
 'https://via.placeholder.com/400x600?text=Naruto', 72, true, NOW(), NOW()),

(3, 1, 'Death Note', 
 '죽음의 노트를 얻은 천재 고등학생과 명탐정의 두뇌 싸움. 2003년부터 2006년까지 연재된 심리 스릴러의 걸작.',
 'Tsugumi Ohba', 'Takeshi Obata', 'Mystery, Thriller, Supernatural', 'COMPLETED', 
 'https://via.placeholder.com/400x600?text=Death+Note', 12, true, NOW(), NOW()),

(4, 1, 'Demon Slayer', 
 '가족을 잃은 소년이 검술을 익혀 악귀들과 싸우는 이야기. 2016-2020년 연재. 애니메이션으로 폭발적 인기를 얻은 작품.',
 'Koyoharu Gotouge', 'Koyoharu Gotouge', 'Action, Dark Fantasy', 'COMPLETED', 
 'https://via.placeholder.com/400x600?text=Demon+Slayer', 23, true, NOW(), NOW()),

-- Kodansha 작품
(5, 2, 'Attack on Titan', 
 '거인에게 지배당하는 세계에서 인류의 생존을 건 전쟁. 2009-2021년 연재. 충격적인 스토리와 반전으로 전 세계를 사로잡은 명작.',
 'Hajime Isayama', 'Hajime Isayama', 'Dark Fantasy, Action, Drama', 'COMPLETED', 
 'https://via.placeholder.com/400x600?text=Attack+on+Titan', 34, true, NOW(), NOW()),

(6, 2, 'Fairy Tail', 
 '마법사 길드의 모험을 그린 판타지 액션. 2006-2017년 연재. 우정과 동료애를 중심으로 한 왕도 소년 망가.',
 'Hiro Mashima', 'Hiro Mashima', 'Fantasy, Adventure, Action', 'COMPLETED', 
 'https://via.placeholder.com/400x600?text=Fairy+Tail', 63, true, NOW(), NOW());

-- =====================================================
-- 4. 각 시리즈의 권(Volume) 데이터
-- =====================================================

-- One Piece (1-5권)
INSERT INTO bt_volumes (volume_id, series_id, title, volume_number, description, cover_image_url, price, page_count, isbn, release_date, is_active, created_at, updated_at) VALUES
(1, 1, 'One Piece Vol.1: Romance Dawn', 1, '루피의 모험이 시작된다! 고무고무 열매를 먹고 고무인간이 된 루피가 해적왕을 꿈꾸며 항해를 시작한다.', 
 'https://via.placeholder.com/400x600?text=One+Piece+Vol1', 6.99, 216, '978-1569319017', '1997-12-24', true, NOW(), NOW()),
 
(2, 1, 'One Piece Vol.2: Buggy the Clown', 2, '첫 번째 강적 버기 선장과의 대결! 루피가 동료들을 모으기 시작한다.', 
 'https://via.placeholder.com/400x600?text=One+Piece+Vol2', 6.99, 200, '978-1569319024', '1998-04-03', true, NOW(), NOW()),
 
(3, 1, 'One Piece Vol.3: Don''t Get Fooled Again', 3, '검사 조로가 동료로 합류! 나미와의 만남.', 
 'https://via.placeholder.com/400x600?text=One+Piece+Vol3', 6.99, 216, '978-1569319031', '1998-06-04', true, NOW(), NOW()),

(4, 1, 'One Piece Vol.4: The Black Cat Pirates', 4, '우솝이 등장! 검은고양이 해적단과의 전투.', 
 'https://via.placeholder.com/400x600?text=One+Piece+Vol4', 6.99, 208, '978-1569319048', '1998-09-03', true, NOW(), NOW()),

(5, 1, 'One Piece Vol.5: For Whom the Bell Tolls', 5, '바라티에 레스토랑 편! 요리사 상디 등장.', 
 'https://via.placeholder.com/400x600?text=One+Piece+Vol5', 6.99, 216, '978-1569319055', '1998-11-04', true, NOW(), NOW());

-- Naruto (1-3권)
INSERT INTO bt_volumes (volume_id, series_id, title, volume_number, description, cover_image_url, price, page_count, isbn, release_date, is_active, created_at, updated_at) VALUES
(6, 2, 'Naruto Vol.1: Uzumaki Naruto', 1, '낙제생 나루토의 이야기가 시작된다! 호카게를 꿈꾸는 소년의 첫 발걸음.', 
 'https://via.placeholder.com/400x600?text=Naruto+Vol1', 7.99, 192, '978-1569319000', '1999-09-21', true, NOW(), NOW()),
 
(7, 2, 'Naruto Vol.2: The Worst Client', 2, '첫 임무! 나루토 팀7의 C급 호위 임무가 시작된다.', 
 'https://via.placeholder.com/400x600?text=Naruto+Vol2', 7.99, 200, '978-1569319017', '1999-12-22', true, NOW(), NOW()),
 
(8, 2, 'Naruto Vol.3: Bridge of Courage', 3, '자부자와의 결전! 나루토의 숨겨진 힘이 깨어난다.', 
 'https://via.placeholder.com/400x600?text=Naruto+Vol3', 7.99, 184, '978-1569319024', '2000-03-03', true, NOW(), NOW());

-- Death Note (1-3권)
INSERT INTO bt_volumes (volume_id, series_id, title, volume_number, description, cover_image_url, price, page_count, isbn, release_date, is_active, created_at, updated_at) VALUES
(9, 3, 'Death Note Vol.1: Boredom', 1, '천재 고등학생 야가미 라이토가 데스노트를 줍다. L과의 두뇌싸움이 시작된다.', 
 'https://via.placeholder.com/400x600?text=Death+Note+Vol1', 8.99, 200, '978-1421501680', '2003-12-01', true, NOW(), NOW()),
 
(10, 3, 'Death Note Vol.2: Confluence', 2, 'L이 본격적으로 키라를 추적하기 시작한다. 긴장감 넘치는 심리전.', 
 'https://via.placeholder.com/400x600?text=Death+Note+Vol2', 8.99, 208, '978-1421501697', '2004-04-06', true, NOW(), NOW()),
 
(11, 3, 'Death Note Vol.3: Hard Run', 3, '라이토와 L의 직접 대면! 숨막히는 공방전이 펼쳐진다.', 
 'https://via.placeholder.com/400x600?text=Death+Note+Vol3', 8.99, 192, '978-1421501703', '2004-06-01', true, NOW(), NOW());

-- Demon Slayer (1-3권)
INSERT INTO bt_volumes (volume_id, series_id, title, volume_number, description, cover_image_url, price, page_count, isbn, release_date, is_active, created_at, updated_at) VALUES
(12, 4, 'Demon Slayer Vol.1: Cruelty', 1, '단지로의 가족이 악귀에게 습격당한다. 유일한 생존자 네즈코를 구하기 위한 여정.', 
 'https://via.placeholder.com/400x600?text=Demon+Slayer+Vol1', 7.99, 192, '978-1974700523', '2016-02-15', true, NOW(), NOW()),
 
(13, 4, 'Demon Slayer Vol.2: It Was You', 2, '검술 훈련의 시작! 우로코다키 사콘지의 가르침을 받는다.', 
 'https://via.placeholder.com/400x600?text=Demon+Slayer+Vol2', 7.99, 192, '978-1974700530', '2016-05-02', true, NOW(), NOW()),
 
(14, 4, 'Demon Slayer Vol.3: Believe in Yourself', 3, '최종 선별 시험! 단지로가 귀살대원이 되다.', 
 'https://via.placeholder.com/400x600?text=Demon+Slayer+Vol3', 7.99, 208, '978-1974700547', '2016-08-04', true, NOW(), NOW());

-- Attack on Titan (1-3권)
INSERT INTO bt_volumes (volume_id, series_id, title, volume_number, description, cover_image_url, price, page_count, isbn, release_date, is_active, created_at, updated_at) VALUES
(15, 5, 'Attack on Titan Vol.1', 1, '100년간의 평화가 깨진다! 초대형 거인의 출현과 인류의 위기.', 
 'https://via.placeholder.com/400x600?text=Attack+on+Titan+Vol1', 8.99, 194, '978-1612620244', '2009-09-09', true, NOW(), NOW()),
 
(16, 5, 'Attack on Titan Vol.2', 2, '훈련병 에렌의 결의! 거인을 쓰러트리기 위한 훈련이 시작된다.', 
 'https://via.placeholder.com/400x600?text=Attack+on+Titan+Vol2', 8.99, 192, '978-1612620251', '2009-12-09', true, NOW(), NOW()),
 
(17, 5, 'Attack on Titan Vol.3', 3, '첫 실전! 트로스트 구 탈환 작전이 시작된다.', 
 'https://via.placeholder.com/400x600?text=Attack+on+Titan+Vol3', 8.99, 192, '978-1612620268', '2010-04-09', true, NOW(), NOW());

-- Fairy Tail (1-3권)
INSERT INTO bt_volumes (volume_id, series_id, title, volume_number, description, cover_image_url, price, page_count, isbn, release_date, is_active, created_at, updated_at) VALUES
(18, 6, 'Fairy Tail Vol.1', 1, '불의 용멸마도사 나츠와 별정령 마도사 루시의 만남! 페어리테일 길드의 모험 시작.', 
 'https://via.placeholder.com/400x600?text=Fairy+Tail+Vol1', 7.99, 208, '978-0345501332', '2006-08-02', true, NOW(), NOW()),
 
(19, 6, 'Fairy Tail Vol.2', 2, '첫 번째 팀워크! 나츠, 루시, 해피의 조합이 만들어진다.', 
 'https://via.placeholder.com/400x600?text=Fairy+Tail+Vol2', 7.99, 192, '978-0345501349', '2006-12-15', true, NOW(), NOW()),
 
(20, 6, 'Fairy Tail Vol.3', 3, '다크 길드와의 대결! 페어리테일의 진정한 힘이 드러난다.', 
 'https://via.placeholder.com/400x600?text=Fairy+Tail+Vol3', 7.99, 192, '978-0345501356', '2007-04-17', true, NOW(), NOW());

-- =====================================================
-- 5. 샘플 리뷰 데이터
-- =====================================================

INSERT INTO bt_reviews (review_id, user_id, volume_id, rating, title, content, like_count, created_at, updated_at) VALUES
(1, 4, 1, 5, 'The beginning of a legendary adventure!', 
 'One Piece Vol.1 is an absolute masterpiece! Oda''s storytelling is phenomenal from the very first chapter. Luffy''s dream to become the Pirate King immediately hooks you. The art style is unique and the world-building is incredible. This is where the greatest manga journey begins!', 
 42, NOW(), NOW()),

(2, 5, 1, 5, '冒険の始まり！最高！', 
 'ルフィの冒険が始まる第1巻！ワンピースの素晴らしさがここから始まります。尾田先生の世界観とキャラクター作りが本当に素晴らしい。何度読んでも飽きない名作です！', 
 35, NOW(), NOW()),

(3, 6, 6, 5, '나루토 시작! 완벽한 첫 권', 
 '나루토의 시작을 알리는 1권. 낙제생이었던 나루토가 어떻게 성장하는지 기대가 됩니다. 키시모토 작가님의 그림체와 스토리 전개가 정말 훌륭합니다. 닌자 세계관도 매력적이고 캐릭터들도 개성있어요!', 
 28, NOW(), NOW()),

(4, 4, 9, 5, 'Mind-blowing psychological thriller!', 
 'Death Note is a game-changer in manga. The cat-and-mouse game between Light and L is absolutely brilliant. Every page keeps you on the edge of your seat. The artwork by Obata is stunning and complements the dark story perfectly.', 
 56, NOW(), NOW()),

(5, 5, 15, 5, '衝撃的な第1巻', 
 '進撃の巨人の第1巻は衝撃的でした。巨人の恐怖と人類の絶望が見事に描かれています。諫山先生の独特な画風と緊張感のあるストーリーテリングが素晴らしい。この先の展開が気になって仕方ありません！', 
 48, NOW(), NOW());

-- =====================================================
-- 6. 샘플 주문 데이터
-- =====================================================

-- 주문 1: john.smith가 One Piece 1-3권 구매
INSERT INTO bt_orders (order_id, user_id, order_number, total_amount, discount_amount, final_amount, status, payment_method, created_at, updated_at) VALUES
(1, 4, 'ORD20250125001', 20.97, 0, 20.97, 'PAID', 'CREDIT_CARD', NOW(), NOW());

INSERT INTO bt_order_items (order_item_id, order_id, volume_id, price, quantity, subtotal, created_at, updated_at) VALUES
(1, 1, 1, 6.99, 1, 6.99, NOW(), NOW()),
(2, 1, 2, 6.99, 1, 6.99, NOW(), NOW()),
(3, 1, 3, 6.99, 1, 6.99, NOW(), NOW());

-- 주문 2: sakura_jp가 Naruto 전권 구매
INSERT INTO bt_orders (order_id, user_id, order_number, total_amount, discount_amount, final_amount, status, payment_method, created_at, updated_at) VALUES
(2, 5, 'ORD20250125002', 23.97, 0, 23.97, 'COMPLETED', 'CREDIT_CARD', NOW(), NOW());

INSERT INTO bt_order_items (order_item_id, order_id, volume_id, price, quantity, subtotal, created_at, updated_at) VALUES
(4, 2, 6, 7.99, 1, 7.99, NOW(), NOW()),
(5, 2, 7, 7.99, 1, 7.99, NOW(), NOW()),
(6, 2, 8, 7.99, 1, 7.99, NOW(), NOW());

-- 주문 3: minho_kr가 Death Note + Attack on Titan 구매
INSERT INTO bt_orders (order_id, user_id, order_number, total_amount, discount_amount, final_amount, status, payment_method, created_at, updated_at) VALUES
(3, 6, 'ORD20250125003', 35.94, 0, 35.94, 'PAID', 'PAYPAL', NOW(), NOW());

INSERT INTO bt_order_items (order_item_id, order_id, volume_id, price, quantity, subtotal, created_at, updated_at) VALUES
(7, 3, 9, 8.99, 1, 8.99, NOW(), NOW()),
(8, 3, 10, 8.99, 1, 8.99, NOW(), NOW()),
(9, 3, 11, 8.99, 1, 8.99, NOW(), NOW()),
(10, 3, 15, 8.99, 1, 8.99, NOW(), NOW());

-- =====================================================
-- 완료!
-- =====================================================

SELECT '✅ Sample data inserted successfully!' as status;
SELECT 'Publishers:', COUNT(*) FROM bt_publishers;
SELECT 'Users:', COUNT(*) FROM bt_users;
SELECT 'Series:', COUNT(*) FROM bt_series;
SELECT 'Volumes:', COUNT(*) FROM bt_volumes;
SELECT 'Reviews:', COUNT(*) FROM bt_reviews;
SELECT 'Orders:', COUNT(*) FROM bt_orders;
