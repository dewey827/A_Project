package com.project.reservation.entity;

import com.project.reservation.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(length = 500)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Column(nullable = false, length = 50)
    private String addr;

    @Column(nullable = false, length = 8)
    private String birth;

    @Column(length = 13)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private Role roles;

    private String provider;
    private String providerId;

    //=============================================================


    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets;

//    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Question> questions;
//
//    @OneToMany(mappedBy = "admin",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Answer> answers;
//
//    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Reservation> reservations;

    //=============================================================
    @Builder
    public Member(String name, String email, String password, String nickName, String addr,
                  String birth, String phone, Role roles, String provider, String providerId, List<Pet> pets) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
        this.roles = roles;
        this.provider = provider;
        this.providerId = providerId;
        this.pets = pets;
    }

    public void updateOAuth2Info(String provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }
    //======================================================================================
    // 수정
    public void updateMember(String password, String nickName, String addr, String phone) {
        this.password = password;
        this.nickName = nickName;
        this.addr = addr;
        this.phone = phone;
    }

    public void resetPassword(String newPassword){
        this.password = newPassword;
    }

    public void setPets(List<Pet> pets) {
        this.pets = new ArrayList<>();
        if (pets != null) {
            for (Pet pet : pets) {
                this.pets.add(pet);
                pet.setMember(this);
            }
        }
    }



//    // 동물 추가
//    public void addPet(Pet pet) {
//        pet.setMember(this);
//        this.pets.add(pet);
//    }
//
//    // 동물 삭제
//    public void removePet(Pet pet) {
//        pet.setMember(null);
//        this.pets.remove(pet);
//    }

    //======================================================================================
    // implements Userdetails - 스프링 시큐리티 overrider 메소드
    // 추후에 role 들이 추가될 수 있으므로 ArrayList<>() 로 관리
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.roles.name()));
        return authorities;
    }

    // 사용자의 id 반환 (이메일)
    @Override
    public String getUsername() {
        return email;
    }

    // 계정 만료 여부 반환 - ture 만료되지 않음
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부 반환 - ture 만료되지 않음
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 패스워드의 만료여부 반환 - true 만료되지 않음
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 사용 가능 여부 반환 - ture 사용가능
    @Override
    public boolean isEnabled() {
        return true;
    }

}