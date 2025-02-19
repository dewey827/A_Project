package com.project.reservation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    // Cross-Origin Resource Sharing (CORS)
    // 웹 브라우저에서 실행되는 스크립트가 다른 출처의 리소스에 접근할 수 있도록 허용하는 메커니즘

    // CorsConfigurationSource - CORS 설정을 정의하는 인터페이스. HTTP 요청에 대한 CORS 구성을 제공
    // 인터페이스 CorsConfigurationSource의 구현체인 UrlBasedCorsConfigurationSource 를 객체를 빈으로 등록 하는 역할을 한다
    @Primary    //02.18 'required a single bean, but 2 were found' 오류때문에 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CorsConfiguration - CORS 설정을 정의하는 클래스. CORS 정책의 세부 사항을 구성

        CorsConfiguration configuration = new CorsConfiguration();

        // CorsConfiguration 에 포함된 메소드 (setAllowedOrigins) 허용된 출처 설정 - 프론트엔드 개발 서버의 주소
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // CorsConfiguration 에 포함된 메소드 (setAllowedMethods) 허용된 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "OPTIONS", "PATCH", "DELETE"));

        // CorsConfiguration 에 포함된 메소드 (setAllowedHeaders)허용된 헤더 설정 -  "Content-Type"과 "Authorization" 헤더를 허용
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));

        // UrlBasedCorsConfigurationSource - CorsConfigurationSource 인터페이스의 구현체. CORS 설정을 URL 패턴별로 적용할 수 있게 해주는 클래스.
        // 새로운 UrlBasedCorsConfigurationSource 객체를 생성해서 source 이름으로 대입한다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // registerCorsConfiguration 메소드 - "/**" 로 설정해서 완전히 모든 엔드포인트에 위에서 만든 CORS 설정 적용
        // 개발 환경에서 프론트엔드와 백엔드를 분리하여 실행하기 때문에
        source.registerCorsConfiguration("/**", configuration);

        // CORS 설정이 적용된 UrlBasedCorsConfigurationSource 객체를 리턴
        return source;
    }
}