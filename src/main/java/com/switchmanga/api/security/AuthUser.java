package com.switchmanga.api.security;

import java.lang.annotation.*;

/**
 * 현재 인증된 사용자를 Controller 파라미터에 주입하기 위한 어노테이션
 * 
 * 사용 예시:
 * @GetMapping("/me")
 * public ResponseEntity<UserResponse> getMe(@AuthUser User user) {
 *     return ResponseEntity.ok(UserResponse.from(user));
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthUser {
}
