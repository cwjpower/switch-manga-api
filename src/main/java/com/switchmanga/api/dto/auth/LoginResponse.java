package com.switchmanga.api.dto.auth;

import com.switchmanga.api.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long userId;
    private String email;
    private String username;
    private UserRole role;
}
