package com.project.reservation.security.oauth;

public interface OAuth2Response {

    String getProvider();
    // oauth 제공자가 부여한 ID
    String getProviderId();

    String getEmail();

    String getName();
}
