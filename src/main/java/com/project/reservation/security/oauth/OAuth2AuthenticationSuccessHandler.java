//package com.project.reservation.security.oauth;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.reservation.security.jwt.JwtTokenUtil;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor

//public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//    private final JwtTokenUtil jwtTokenUtil;
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        //OAuth2User
//        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//        String nickname = customOAuth2User.getUsername(); // 사용자 정보에서 닉네임 추출
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
//
//        String token = jwtTokenUtil.generateToken(customOAuth2User, nickname);
//
//        response.addCookie(createCookie("Authorization", token));
//        response.sendRedirect("http://localhost:3000/");
//    }
//
//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(60*60*60*60);
//        //cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//
//        return cookie;
//    }
//}


//public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtTokenUtil jwtTokenUtil;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//        String nickname = customOAuth2User.getUsername(); // 사용자 정보에서 닉네임 추출
//        String token = jwtTokenUtil.generateToken(customOAuth2User, nickname);
//
//        // JSON 응답 생성
//        Map<String, String> tokenResponse = new HashMap<>();
//        tokenResponse.put("token", token);
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
//    }
//}

package com.project.reservation.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.reservation.dto.response.member.ResMemberToken;
import com.project.reservation.security.jwt.JwtTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
//@RequiredArgsConstructor
//public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtTokenUtil jwtTokenUtil;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String nickname = userDetails.getUsername(); // 사용자 정보에서 닉네임 추출
//        String token = jwtTokenUtil.generateToken(userDetails, nickname);
//        response.addHeader("Authorization", "Bearer " + token);
//        response.sendRedirect("/");
//    }
//}


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String nickname = customOAuth2User.getUsername();
        String token = jwtTokenUtil.generateToken(customOAuth2User, nickname);

        ResMemberToken resMemberToken = ResMemberToken.fromEntity(customOAuth2User, token);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(resMemberToken));
    }
}

