package com.switchmanga.api.controller;

import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.UserRole;
import com.switchmanga.api.entity.UserStatus;
import com.switchmanga.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원 관리 API - 사용자 등록, 조회, 수정, 삭제 기능을 제공합니다")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "전체 회원 조회",
        description = "시스템에 등록된 모든 회원 정보를 조회합니다. 관리자 전용 기능입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "회원 상세 조회",
        description = "특정 회원의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
        @Parameter(description = "회원 ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "이메일로 회원 조회",
        description = "이메일 주소로 회원 정보를 조회합니다."
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
        @Parameter(description = "이메일 주소", required = true, example = "user@example.com")
        @PathVariable String email
    ) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "사용자명으로 회원 조회",
        description = "사용자명(username)으로 회원 정보를 조회합니다."
    )
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
        @Parameter(description = "사용자명", required = true, example = "john_doe")
        @PathVariable String username
    ) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "권한별 회원 조회",
        description = "특정 권한을 가진 회원 목록을 조회합니다."
    )
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(
        @Parameter(description = "사용자 권한", required = true, example = "USER")
        @PathVariable UserRole role
    ) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "상태별 회원 조회",
        description = "특정 상태의 회원 목록을 조회합니다."
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(
        @Parameter(description = "회원 상태", required = true, example = "ACTIVE")
        @PathVariable UserStatus status
    ) {
        List<User> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "활성+인증 회원 조회",
        description = "활성화되고 이메일 인증이 완료된 회원 목록을 조회합니다."
    )
    @GetMapping("/active/verified")
    public ResponseEntity<List<User>> getActiveVerifiedUsers() {
        List<User> users = userService.getActiveVerifiedUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "이메일로 회원 검색",
        description = "이메일 주소로 회원을 검색합니다. (부분 일치)"
    )
    @GetMapping("/search/email")
    public ResponseEntity<List<User>> searchUsersByEmail(
        @Parameter(description = "검색할 이메일", required = true, example = "user")
        @RequestParam String email
    ) {
        List<User> users = userService.searchUsersByEmail(email);
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "사용자명으로 회원 검색",
        description = "사용자명으로 회원을 검색합니다. (부분 일치)"
    )
    @GetMapping("/search/username")
    public ResponseEntity<List<User>> searchUsersByUsername(
        @Parameter(description = "검색할 사용자명", required = true, example = "john")
        @RequestParam String username
    ) {
        List<User> users = userService.searchUsersByUsername(username);
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "권한별 회원 수 조회",
        description = "특정 권한을 가진 회원의 수를 조회합니다."
    )
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> countUsersByRole(
        @Parameter(description = "사용자 권한", required = true)
        @PathVariable UserRole role
    ) {
        Long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(count);
    }

    @Operation(
        summary = "상태별 회원 수 조회",
        description = "특정 상태의 회원 수를 조회합니다."
    )
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countUsersByStatus(
        @Parameter(description = "회원 상태", required = true)
        @PathVariable UserStatus status
    ) {
        Long count = userService.countUsersByStatus(status);
        return ResponseEntity.ok(count);
    }

    @Operation(
        summary = "회원 가입",
        description = "새로운 회원을 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일 중복 등)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<User> createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원 정보",
            required = true
        )
        @RequestBody User user
    ) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "회원 정보 수정",
        description = "회원의 정보를 수정합니다."
    )
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable Long id,
        @RequestBody User user
    ) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "비밀번호 변경",
        description = "회원의 비밀번호를 변경합니다."
    )
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "새 비밀번호", required = true)
        @RequestParam String newPassword
    ) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "권한 변경",
        description = "회원의 권한을 변경합니다. 관리자 전용 기능입니다."
    )
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> changeRole(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "새 권한", required = true)
        @RequestParam UserRole role
    ) {
        User updated = userService.changeRole(id, role);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "상태 변경",
        description = "회원의 상태를 변경합니다. (활성화/정지/삭제)"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> changeStatus(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "새 상태", required = true)
        @RequestParam UserStatus status
    ) {
        User updated = userService.changeStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "이메일 인증",
        description = "회원의 이메일을 인증 완료 처리합니다."
    )
    @PatchMapping("/{id}/verify")
    public ResponseEntity<User> verifyEmail(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable Long id
    ) {
        User verified = userService.verifyEmail(id);
        return ResponseEntity.ok(verified);
    }

    @Operation(
        summary = "마지막 로그인 시간 업데이트",
        description = "회원의 마지막 로그인 시간을 현재 시간으로 업데이트합니다."
    )
    @PatchMapping("/{id}/last-login")
    public ResponseEntity<Void> updateLastLogin(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable Long id
    ) {
        userService.updateLastLogin(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "회원 삭제",
        description = "회원을 삭제합니다. 실제로는 상태를 DELETED로 변경합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
