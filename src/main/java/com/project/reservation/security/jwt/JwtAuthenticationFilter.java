package com.project.reservation.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
// OncePerRequestFilter HTTP 요청당 한 번만 실행되는 것을 보장하는 필터
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // UserDetailsService - 사용자 정보를 로드하는 역할.
    // 이미 스프링 시큐리티에 정의되어 있는 인터페이스이지만, 실제로는 이를 내가 직접 구현한 CustomUserDetailsService 를 가져온다.
    // 구체적인 구현체와의 결합도를 낮추는 유연성 위해 UserDetailsService userDetailsService; 로 주입한다.
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    private  final  CustomUserDetailsService customUserDetailsService;

    // 프로퍼티에서 해당 문자열 받아와서 HEADER_STRING, TOKEN_PREFIX 변수로 할당
    @Value("${jwt.header}") private String HEADER_STRING;
    @Value("${jwt.prefix}") private String TOKEN_PREFIX;

    // doFilterInternal - OncePerRequestFilter 클래스에 정의된 메소드
    // 모든 HTTP 요청을 가로채기, JWT 를 추출하고 검증하기, 검증된 토큰 정보를 바탕으로 인증 객체를 생성하고 컨텍스트에 설정하기,
    // filterChain.doFilter(request, response)를 호출하여 다음 필터 또는 최종 목적지로 요청 전달하기,
    // HttpServletRequest 와 HttpServletResponse 객체를 통해 요청과 응답을 조작하기,
    // ServletException 이나 IOException 을 던져서 예외 처리하기 를 수행한다.
    // 따라서, 메소드 선언부는 사실상 반 고정된 형태로, 순서나 타입을 변경하면 안됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 웹 애플리케이션은 여러 사용자의 요청을 동시에 처리. 각 요청은 별도의 스레드에서 처리, 현재 실행 중인 스레드의 요청 처리의 동시성을 모니터링
        Thread currentThread = Thread.currentThread();
        log.info("현재 실행 중인 스레드: " + currentThread.getName());

        // request.getHeader 로 HTTP 요청의 Authorization 헤더에서 Bearer 로 시작하는 문자열을 찾음. Bearer lkshg.afuqwo.cxoih 같은 형태로.
        // getHeader 라고 해서 lkshg 같은 진짜 헤더 클레임만 가져오는 것이 아니라 Bearer lkshg.afuqwo.cxoih 같은 bearer 를 포함한 전체 JWT 가 가져옴
        String header = request.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;

        // Bearer lkshg.afuqwo.cxoih 가 null 이 아니고, Bearer 로 시작하면
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            // Bearer lkshg.afuqwo.cxoih 에서 Bearer 를 공백으로 대체해서 (순수 토큰만 분리해서) 미리 초기화한 authToken 에 대입
            authToken = header.replace(TOKEN_PREFIX," ");
            // jwtTokenUtil 의 getUsernameFromToken 메소드 사용해서 클레임에서 getSubject 로 사용자 이름만 추출해서 초기화해둔 username 에 대입
            // .this 는 멤버변수임을 확실히 하기 위해 사용되었고, 빼고 사용해도 문제없다.
            // IllegalArgumentException: 잘못된 인자 (예: 널 토큰), ExpiredJwtException: 만료된 토큰,
            // MalformedJwtException: 잘못된 형식의 JWT, Exception: 기타 예외 상황.
            try {
                username = this.jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException ex) {
                log.info("JWT 의 형식이 올바르지 않음 !!");
                ex.printStackTrace();
            } catch (ExpiredJwtException ex) {
                log.info("JWT 만료됨 !!");
                ex.printStackTrace();
            } catch (MalformedJwtException ex) {
                log.info("JWT 형식이 잘못됨 !!");
                System.out.println();
                ex.printStackTrace();
            } catch (Exception e) {
                log.info("JWT 오류 !!");
                e.getStackTrace();
            }
        // Bearer lkshg.afuqwo.cxoih 가 null 이거나, Bearer 로 시작하지 않으면
        } else {
            log.info("JWT 이 Bearer 로 시작하지 않거나, header 가 null 임 !!");
        }

        // SecurityContextHolder - 서버에서 JWT 토큰이 유효하다고 판단되면, Authentication 객체를 만들고 이를 SecurityContextHolder 에 설정
        // getContext() - SecurityContextHolder 에서 현재 SecurityContext 를 가져옴
        // getAuthentication() - SecurityContext 에서 현재 Authentication 객체를 가져옴

        // (위에서 username 에 사용자 이름이 대입된 상태) username 이 null 이 아니고, Authentication 이 null 이면 =
        if ((username != null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
            // loadUserByUsername 한 UserDetails 객체를 userDetails 이름으로 대입
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // validateToken - 순수 토큰인 authToken 의 토큰에서 추출한 사용자 이름이 UserDetails 객체의 사용자 이름과 일치하고, 토큰이 만료되지 않았다면
            if (this.jwtTokenUtil.validateToken(authToken, userDetails)) {


                // JWT에서 roles 정보 추출
                List<String> roles = jwtTokenUtil.getRolesFromToken(authToken);

                // roles를 GrantedAuthority 객체로 변환
                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                /** 토큰 생성단계에서 Role 추가로 변경
                // UsernamePasswordAuthenticationToken - ID(principal)와 비밀번호(credentials)를 기반으로 인증을 처리하는 클래스
                // userDetails 객체, 보안을 위해 비밀번호를 null 로, 사용자의 권한 정보 를 가져와서 authenticationToken 에 대입
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                **/

                // authenticationToken - 인증 객체
                // authenticationToken 에 추가적인 세부정보 설정.
                // WebAuthenticationDetailsSource - AuthenticationDetailsSource 인터페이스의 구현체. HttpServletRequest 객체로부터 인증 세부 정보를 생성
                // .buildDetails(request) - HttpServletRequest 객체를 매개변수로 받아 WebAuthenticationDetails 객체를 생성
                // .setDetails - remoteAddress (클라이언트의 IP 주소), sessionId (현재 세션의 ID) 의 정보를 인증 토큰의 details 속성에 설정
                authenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("인증된 사용자 : " + username + ", Security Context 설정함.");
                // SecurityContextHolder 에서 현재 SecurityContext 에 생성된 인증객체 authenticationToken 를 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);



            } else {
                log.info("authToken 의 토큰에서 추출한 사용자 이름이 UserDetails 객체의 사용자 이름과 일치하지 않거나, 토큰이 만료 !!");
            }

        // username 이 null 이거나, Authentication 이 null 이 아니다
        } else {
            log.info("username 이 null 이거나, Authentication 이 null 이 아님 !!");
        }
        // 현재 필터의 처리를 완료하고 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }
}
