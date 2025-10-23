package com.switchmanga.api.repository;

import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.UserRole;
import com.switchmanga.api.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 조회 (로그인용)
    Optional<User> findByEmail(String email);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // 사용자명으로 조회
    Optional<User> findByUsername(String username);

    // 사용자명 중복 체크
    boolean existsByUsername(String username);

    // 권한별 조회
    List<User> findByRole(UserRole role);

    // 상태별 조회
    List<User> findByStatus(UserStatus status);

    // 활성 회원만 조회
    List<User> findByStatusAndEmailVerifiedTrue(UserStatus status);

    // 이메일로 검색 (부분 일치)
    List<User> findByEmailContaining(String email);

    // 사용자명으로 검색 (부분 일치)
    List<User> findByUsernameContaining(String username);

    // 권한별 회원 수
    Long countByRole(UserRole role);

    // 상태별 회원 수
    Long countByStatus(UserStatus status);
}