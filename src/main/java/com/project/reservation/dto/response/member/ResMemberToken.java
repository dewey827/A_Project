package com.project.reservation.dto.response.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
public class ResMemberToken {

    private String email;
    private String token;

    @Builder
    public ResMemberToken(String email, String token) {
        this.email = email;
        this.token = token;
    }

    // Entity -> DTO
    public static ResMemberToken fromEntity(UserDetails user, String token) {
        return ResMemberToken.builder()
                .email(user.getUsername())
                .token(token)
                .build();
    }
}