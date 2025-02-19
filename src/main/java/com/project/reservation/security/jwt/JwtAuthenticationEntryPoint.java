package com.project.reservation.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// AuthenticationEntryPoint 인터페이스 구현. 인증되지 않은 접근 시 발생하는 예외를 처리하는 역할
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // AuthenticationEntryPoint 는 commence 라는 하나의 메소드만 가짐. 선언부는 반 고정된 형태
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // JwtAuthenticationFilter 의 예외 처리 추가함.
        String errorMessage = "권한 없음";

        if (authException.getCause() instanceof IllegalArgumentException) {
            errorMessage = "JWT 의 형식이 올바르지 않음 !!";
        } else if (authException.getCause() instanceof ExpiredJwtException) {
            errorMessage = "JWT 만료됨 !!";
        } else if (authException.getCause() instanceof MalformedJwtException) {
            errorMessage = "JWT 형식이 잘못됨 !!";
        }

        // response.sendError - HttpServletResponse 의 메소드
        // 응답의 상태코드를 401로 설정, 응답 본문에 "Unauthorized" 메시지를 포함
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }
}