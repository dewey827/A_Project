package com.project.reservation.security.oauth;

import com.project.reservation.entity.Member;
import com.project.reservation.entity.Role;
import com.project.reservation.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        final ResOAuth2 resOAuth2;

        if (registrationId.equals("naver")) {
            resOAuth2 = new ResNaver(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            resOAuth2 = new ResGoogle(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String email = resOAuth2.getEmail();
        String provider = resOAuth2.getProvider();
        String providerId = resOAuth2.getProviderId();

        Member member = memberRepository.findByEmail(email)
                .map(existingMember -> {
                    existingMember.updateOAuth2Info(provider, providerId);
                    return memberRepository.save(existingMember);
                })  // 이미 존재하는 회원의 이메일인 경우, 회원의 엔티티 업데이트
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(email)
                            .name("social_"+ resOAuth2.getName())
                            .nickName("social_"+ resOAuth2.getName())
                            .roles(Role.USER)
                            .provider(provider)
                            .providerId(providerId)
                            .addr("") // 필수 필드
                            .birth("") // 필수 필드
                            .build();
                    return memberRepository.save(newMember);
                });
        return new CustomOAuth2User(resOAuth2,"ROLE_" + member.getRoles().name());
    }
}
