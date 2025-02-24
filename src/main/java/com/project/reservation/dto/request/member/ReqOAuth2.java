package com.project.reservation.dto.request.member;

import com.project.reservation.entity.Member;
import com.project.reservation.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqOAuth2 {

    private String name;
    private String email;
    private String nickName;
    private String provider;
    private String providerId;

    public ReqOAuth2(String name, String email, String nickName, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.nickName = nickName;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Builder


    public Member ofEntity(ReqOAuth2 reqOAuth2) {
        return Member.builder()
                .name(reqOAuth2.getName())
                .email(reqOAuth2.getEmail())
                .nickName(reqOAuth2.getNickName())
                .provider(reqOAuth2.getProvider())
                .providerId(reqOAuth2.getProviderId())
                .roles(Role.USER)
                .build();
    }
}
