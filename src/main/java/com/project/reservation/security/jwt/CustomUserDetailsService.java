package com.project.reservation.security.jwt;

import com.project.reservation.common.exception.ResourceNotFoundException;
import com.project.reservation.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService - 사용자 인증 정보를 로드하는데 사용하는 인터페이스.
// JwtAuthenticationFilter 에서 부모 인터페이스인 UserDetailsService 로 주입한다.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // username(이메일)으로 사용자 정보를 로드해서 UserDetails 객체로 반환
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 리파지토리에서 이메일로 사용자를 찾고, 조회한 UserDetails 객체를 리턴. 없으면 ResourceNotFoundException 발생.
        return this.memberRepository.findByEmail(username).orElseThrow(
                () -> new ResourceNotFoundException("Member", "Member Email : ", username));
    }
}

/*
 * 1. 사용자가 로그인을 시도하면 Spring Security 인증 과정 시작.
 * 2. UserDetailsService 의 구현체 CustomUserDetailsService 가 호출.
 * 3. UserDetailsService 의 loadUserByUsername 메서드가 매개변수로 username 을 받아서 실행
 * 4. memberRepository 에서 findByEmail 메소드를 불러와 데이터베이스에서 사용자 정보를 조회, 없으면 예외발생
 * 5. 조회된 사용자 정보를 바탕으로 UserDetails 객체가 반환
 */


