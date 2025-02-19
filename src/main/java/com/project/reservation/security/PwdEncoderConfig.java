package com.project.reservation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// BCryptPasswordEncoder 객체를 생성해서 passwordEncoder() 이름의 Bean 으로 등록하는 클래스
// 이 설정을 별도의 클래스로 분리해서 다른 보안 설정과의 순환참조 방지
@Configuration
public class PwdEncoderConfig {

    // BCryptPasswordEncoder - 비밀번호를 암호화는 클래스
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}