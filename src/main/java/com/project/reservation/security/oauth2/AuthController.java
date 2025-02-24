package com.project.reservation.security.oauth2;

import com.project.reservation.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/login/google")
    public ResponseEntity<?> googleLogin() {
        return ResponseEntity.ok().body("Google 로그인 페이지로 리다이렉트됩니다.");
    }

    @GetMapping("/oauth2/callback/google")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        // 이 메서드는 실제로 사용되지 않습니다. OAuth2 로그인 프로세스는 Spring Security에 의해 자동으로 처리됩니다.
        return ResponseEntity.ok().body("Google 로그인 성공");
    }
}
