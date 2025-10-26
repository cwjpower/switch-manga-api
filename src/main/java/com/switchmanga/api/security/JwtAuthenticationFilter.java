package com.switchmanga.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // ⭐ 필터를 건너뛸 경로 목록 (Public API만!)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v1/auth/",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/api/v1/publishers",
            "/api/v1/series",
            "/api/v1/volumes"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        log.debug("=== JWT Filter Check ===");
        log.debug("Request URI: {}", path);
        
        // ⭐ Admin 경로는 항상 필터 통과 (인증 필요!)
        if (path.contains("/admin")) {
            log.debug("Admin path detected - filter will process");
            return false;
        }
        
        // ⭐ Public API는 필터 건너뛰기
        boolean shouldSkip = EXCLUDED_PATHS.stream().anyMatch(excluded -> {
            // 정확히 일치하거나, 숫자 ID가 뒤에 오는 경우만 건너뛰기
            if (path.equals(excluded)) {
                return true;
            }
            // /api/v1/publishers/123 같은 경우 (숫자 ID)
            if (path.startsWith(excluded + "/")) {
                String remaining = path.substring((excluded + "/").length());
                // 첫 번째 세그먼트가 숫자인 경우만
                String firstSegment = remaining.split("/")[0];
                return firstSegment.matches("\\d+");
            }
            return false;
        });
        
        log.debug("Should skip filter: {}", shouldSkip);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String email = jwtTokenProvider.getEmailFromToken(jwt);
                String role = jwtTokenProvider.getRoleFromToken(jwt);

                log.debug("JWT validated for user: {} with role: {}", email, role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("SecurityContext updated with authentication");
            } else {
                log.debug("No valid JWT token found");
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
