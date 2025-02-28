package com.project.reservation.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.reservation.dto.response.member.ResMemberToken;
import com.project.reservation.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String nickname = customOAuth2User.getName();
        String modifiedNickname = "social_" + nickname;

        String token = jwtTokenUtil.generateToken(customOAuth2User, modifiedNickname);

        // 추가
        response.addHeader("Authorization", "Bearer " + token);

        ResMemberToken resMemberToken = ResMemberToken.fromEntity(customOAuth2User, token);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(resMemberToken));
    }
}