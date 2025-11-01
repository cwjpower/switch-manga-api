package com.switchmanga.api.config;

import com.switchmanga.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ========================================
                        // 1. Public API (인증 불필요)
                        // ========================================
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/test").permitAll()
                        .requestMatchers("/api/v1/upload/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        
                        // Publisher Public API
                        .requestMatchers("/api/v1/publishers").permitAll()
                        .requestMatchers("/api/v1/publishers/{id}").permitAll()
                        
                        // Series Public API
                        .requestMatchers("/api/v1/series").permitAll()
                        .requestMatchers("/api/v1/series/{id}").permitAll()
                        .requestMatchers("/api/v1/series/{id}/volumes").permitAll()
                        
                        // Volume Public API
                        .requestMatchers("/api/v1/volumes").permitAll()
                        .requestMatchers("/api/v1/volumes/{id}").permitAll()
                        
                        // ========================================
                        // 2. Publisher Portal API (인증 필요) ✅
                        // ========================================
                        .requestMatchers("/api/v1/publishers/me/**").authenticated()
                        
                        // ========================================
                        // 3. Admin API (인증 필요)
                        // ========================================
                        .requestMatchers("/api/v1/publishers/admin/**").authenticated()
                        .requestMatchers("/api/v1/admin/**").authenticated()
                        
                        // ========================================
                        // 4. 나머지 모든 요청 (인증 필요)
                        // ========================================
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://34.64.84.117:*"
        ));
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
