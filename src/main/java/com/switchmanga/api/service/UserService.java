package com.switchmanga.api.service;

import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.UserRole;
import com.switchmanga.api.entity.UserStatus;
import com.switchmanga.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 전체 회원 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 회원 상세 조회
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. ID: " + id));
    }

    // 이메일로 조회
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. Email: " + email));
    }

    // 사용자명으로 조회
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. Username: " + username));
    }

    // 권한별 조회
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    // 상태별 조회
    public List<User> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }

    // 활성+인증 회원 조회
    public List<User> getActiveVerifiedUsers() {
        return userRepository.findByStatusAndEmailVerifiedTrue(UserStatus.ACTIVE);
    }

    // 이메일로 검색
    public List<User> searchUsersByEmail(String email) {
        return userRepository.findByEmailContaining(email);
    }

    // 사용자명으로 검색
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContaining(username);
    }

    // 권한별 회원 수
    public Long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    // 상태별 회원 수
    public Long countUsersByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }

    // 회원 가입
    @Transactional
    public User createUser(User user) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다: " + user.getEmail());
        }

        // 사용자명 중복 체크
        if (user.getUsername() != null && userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("이미 사용 중인 사용자명입니다: " + user.getUsername());
        }

        // 비밀번호 암호화는 나중에 추가 (지금은 일단 그대로)
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 기본값 설정
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        if (user.getEmailVerified() == null) {
            user.setEmailVerified(false);
        }

        return userRepository.save(user);
    }

    // 회원 정보 수정
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        // 이메일 변경 시 중복 체크
        if (!user.getEmail().equals(userDetails.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("이미 사용 중인 이메일입니다: " + userDetails.getEmail());
            }
            user.setEmail(userDetails.getEmail());
            user.setEmailVerified(false); // 이메일 변경 시 재인증 필요
        }

        // 사용자명 변경 시 중복 체크
        if (userDetails.getUsername() != null && !user.getUsername().equals(userDetails.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("이미 사용 중인 사용자명입니다: " + userDetails.getUsername());
            }
            user.setUsername(userDetails.getUsername());
        }

        user.setPhone(userDetails.getPhone());
        user.setProfileImage(userDetails.getProfileImage());
        user.setBirthDate(userDetails.getBirthDate());
        user.setGender(userDetails.getGender());

        return userRepository.save(user);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = getUserById(id);
        // 비밀번호 암호화는 나중에 추가
        // user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    // 권한 변경
    @Transactional
    public User changeRole(Long id, UserRole role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    // 상태 변경
    @Transactional
    public User changeStatus(Long id, UserStatus status) {
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    // 이메일 인증
    @Transactional
    public User verifyEmail(Long id) {
        User user = getUserById(id);
        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    // 마지막 로그인 시간 업데이트
    @Transactional
    public void updateLastLogin(Long id) {
        User user = getUserById(id);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // 회원 삭제
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}