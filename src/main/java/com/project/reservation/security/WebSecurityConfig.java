//package com.project.reservation.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//
////    private final CustomUserDetailsService userService;
//
//    //정적경로 가져올때는 보안필터가 무시하도록
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers(new AntPathRequestMatcher("/static/**"));
//    }
//
//    //특정경로에 대한 접근을 허용하고, 나머지 요청은 인증이 필요하도록 설정
//    //폼 기반 로그인 설정 - 로그인 페이지를 /login 으로 설정, 성공시 /home 으로 리다이렉트
//    //로그아웃 성공시 /home 으로 리다이렉트, 세션 무효화
//    //CSRF 보호 비활성화 - 메소드 참조
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth
//                        //"" 경로로 요청이 오면 인증/인가 없이 접근 할 수 있도록 permitAll
//                        //anyRequest: 나머지 모든 이외의 요청에는
//                        //authenticated: 별도의 인가는 필요 없지만 인증이 성공해야만 접근
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/"),
//                                new AntPathRequestMatcher("/login"),
//                                new AntPathRequestMatcher("/signup"),
//                                new AntPathRequestMatcher("/user")
//                        ).permitAll()
//                        .anyRequest().authenticated())
//                //loginPage: 로그인페이지로 설정
//                //defaultSuccessUrl: 성공시 이동할 경로
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/", true)
//                )
//                //logoutSuccessUrl: 로그아웃시 이동할 경로
//                //invalidateHttpSession: 로그아웃 이후에 세션을 삭제할지 여부
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/")
//                        .invalidateHttpSession(true)
//                )
//                .httpBasic(withDefaults())
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                )
//                //csrf 공격 보호 비활성화
//                .csrf(AbstractHttpConfigurer::disable)
//                .build();
//    }
//}
//
//    //bCryptPasswordEncoder() 메소드 - BCryptPasswordEncoder 를 빈으로 생성
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    //인증관리자 관련 설정
//    //AuthenticationManager: 반환타입, authenticationManager: 메소드, (매개값 1, 매개값 2, 매개값 3)
//    //DaoAuthenticationProvider 를 사용해서 사용자 세부정보 인증, 비밀번호 인코더로 BCryptPasswordEncoder 설정
//    @Bean
//    public AuthenticationManager authenticationManager(
//            HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, CustomUserDetailsService memberDetailService) throws Exception {
//
//        //DaoAuthenticationProvider: 인증을 위한 제공자, 인증을 위한 로직을 가지고 있음
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        //사용자 정보를 가져올 서비스 클래스 설정 -  UserDetailsService를 상속받은 클래스!
//        authProvider.setUserDetailsService(userService);
//        //비밀번호를 암호화하기 위한 인코더 설정
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
//        //ProviderManager: 스프링시큐리티의 인증 매니저 클래스. 인증 요청을 적절한 AuthenticationProvider에게 위임하여 처리함.(여기선 authProvider)
//        return new ProviderManager(authProvider);
//    }
//
//}
//
//
// 봉인
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/api/register", "/api/login").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin().defaultSuccessURL("/home", true)
//                .and()
//                .logout().logoutSuccessURL("/login");
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
//
//
