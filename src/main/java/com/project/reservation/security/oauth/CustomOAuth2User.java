package com.project.reservation.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final String role;

    @Override
    public Map<String, Object> getAttributes() { return null; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() { return role; }
        });

        return collection;
    }

    @Override
    public String getName() { return oAuth2Response.getName(); }

    // 우리 사이트에서 사용할 ID를 만들어준다
    public String getUsername() {
        return oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
    }
}