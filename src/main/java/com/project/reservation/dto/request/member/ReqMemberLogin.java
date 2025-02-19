package com.project.reservation.dto.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqMemberLogin {

    private String email;
    private String password;

    @Builder
    public ReqMemberLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
