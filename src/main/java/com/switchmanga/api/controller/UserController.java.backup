package com.switchmanga.api.controller;

import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.UserRole;
import com.switchmanga.api.entity.UserStatus;
import com.switchmanga.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 전체 회원 조회
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 회원 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // 이메일로 조회
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // 사용자명으로 조회
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    // 권한별 조회
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable UserRole role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    // 상태별 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable UserStatus status) {
        List<User> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    // 활성+인증 회원 조회
    @GetMapping("/active/verified")
    public ResponseEntity<List<User>> getActiveVerifiedUsers() {
        List<User> users = userService.getActiveVerifiedUsers();
        return ResponseEntity.ok(users);
    }

    // 이메일로 검색
    @GetMapping("/search/email")
    public ResponseEntity<List<User>> searchUsersByEmail(@RequestParam String email) {
        List<User> users = userService.searchUsersByEmail(email);
        return ResponseEntity.ok(users);
    }

    // 사용자명으로 검색
    @GetMapping("/search/username")
    public ResponseEntity<List<User>> searchUsersByUsername(@RequestParam String username) {
        List<User> users = userService.searchUsersByUsername(username);
        return ResponseEntity.ok(users);
    }

    // 권한별 회원 수
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> countUsersByRole(@PathVariable UserRole role) {
        Long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(count);
    }

    // 상태별 회원 수
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countUsersByStatus(@PathVariable UserStatus status) {
        Long count = userService.countUsersByStatus(status);
        return ResponseEntity.ok(count);
    }

    // 회원 가입
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 회원 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    // 비밀번호 변경
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    // 권한 변경
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> changeRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        User updated = userService.changeRole(id, role);
        return ResponseEntity.ok(updated);
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> changeStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {
        User updated = userService.changeStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // 이메일 인증
    @PatchMapping("/{id}/verify")
    public ResponseEntity<User> verifyEmail(@PathVariable Long id) {
        User verified = userService.verifyEmail(id);
        return ResponseEntity.ok(verified);
    }

    // 마지막 로그인 시간 업데이트
    @PatchMapping("/{id}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable Long id) {
        userService.updateLastLogin(id);
        return ResponseEntity.ok().build();
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}