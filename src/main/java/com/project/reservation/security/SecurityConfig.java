package com.project.reservation.security;

import com.project.reservation.security.jwt.JwtAuthenticationEntryPoint;
import com.project.reservation.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    // AuthenticationManager - 인증을 담당하는 매니저. 사용자의 인증 정보(ID, 비밀번호 등)를 검증하고 인증 결과를 반환하는 역할
    // AuthenticationManager 를 생성하려면 AuthenticationConfiguration 을 주입받아서, getAuthenticationManager 메소드를 실행해야 함
    @Bean
    public AuthenticationManager authenticationManager(
            // AuthenticationManager 를 생성하는 데 필요한 설정을 포함. Spring에 의해 자동으로 주입됨
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // getAuthenticationManager - AuthenticationConfiguration 의 메소드, 실제 AuthenticationManager 생성
        return authenticationConfiguration.getAuthenticationManager();
    }

    // SecurityFilterChain - 인터페이스.
    // filterChain - 메소드. HttpSecurity 클래스를 사용하여 보안 설정을 구성하고, 최종적으로 SecurityFilterChain 객체를 생성하여 반환
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // HTTP 기본 인증을 비활성화. JWT 를 사용하려고
        // CSRF(Cross-Site Request Forgery) 보호를 비활성화
        // CORS 활성화. CorsConfigurationSource 에서 만들어둔 CORS 설정을 주입받은 UrlBasedCorsConfigurationSource 객체의 인스턴스 source 와 동일함
        return http
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // authorizeHttpRequests - HTTP 요청에 대한 인가 규칙 설정을 시작 메소드
                // requestMatchers - 특정 HTTP 요청에 대한 보안 규칙을 정의하는 데 사용되는 메소드
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/login",
                                "/api/register",
                                "/question/list",
                                "/review/list",
                                "/review/{reviewId}",
                                "/review/{reviewId}/comment/list",
                                "/notice/list",
                                "/notice/{noticeId}",
                                "/notice/{noticeId}/file/list",
                                "/notice/{noticeId}/file/download/**").permitAll()      // 추가 필요


                        .requestMatchers("/api/member/**").hasRole("USER")      // 추가 필요

                        .requestMatchers("/api/admin/**").hasRole("ADMIN"))     // 추가 필요

                // 서버가 클라이언트 세션을 유지하지 않도록 무상태로 설정 메소드
                // 보안예외처리 구성 메소드. authenticationEntryPoint - 인증되지 않은 사용자가 보호된 리소스에 접근하려 할 때의 동작을 정의.
                // 필터체인에 커스텀 JWT 인증 필터를 추가하는 메소드. JWT 토큰 검증이 먼저 수행해서 추가적인 인증 과정 거치지 않게.
                //JWT 토큰이 유효하지 않거나 없는 경우에만 다음 필터로 진행.
                // HttpSecurity 객체에 설정된 모든 보안 구성을 바탕으로 최종적인 SecurityFilterChain 객체를 생성
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(excep -> excep.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

