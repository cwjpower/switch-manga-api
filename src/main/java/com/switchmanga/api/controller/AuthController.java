package com.switchmanga.api.controller;

import com.switchmanga.api.dto.auth.LoginRequest;
import com.switchmanga.api.dto.auth.LoginResponse;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.UserStatus;
import com.switchmanga.api.repository.UserRepository;
import com.switchmanga.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다"));

            log.info("User found: {}", user.getEmail());
            log.info("DB Password Hash (first 30 chars): {}", user.getPassword().substring(0, 30));
            log.info("Input Password: {}", request.getPassword());

            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            log.info("Password matches: {}", matches);
            
            if (!matches) {
                log.warn("Password mismatch for email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "이메일 또는 비밀번호가 일치하지 않습니다"));
            }

            if (user.getStatus() != UserStatus.ACTIVE) {
                log.warn("Inactive account: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "비활성화된 계정입니다"));
            }

            String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
            log.info("Token generated for: {}", user.getEmail());

            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();

            log.info("Login successful for: {}", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login error: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtTokenProvider.getEmailFromToken(jwt);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(jwt);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }

    @PostMapping("/hash-password")
    public ResponseEntity<?> hashPassword(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        String hashed = passwordEncoder.encode(password);
        
        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        response.put("hashed", hashed);
        response.put("length", String.valueOf(hashed.length()));
        
        return ResponseEntity.ok(response);
    }
}
