package com.project.reservation.security.oauth;

import lombok.RequiredArgsConstructor;
import java.util.Map;

@RequiredArgsConstructor
public class ResNaver implements ResOAuth2 {
    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        Map<String, Object> response = (Map<String, Object>) attribute.get("response");
        return response.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attribute.get("response");
        return response.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attribute.get("response");
        return response.getOrDefault("name", "Unknown").toString();
    }
}