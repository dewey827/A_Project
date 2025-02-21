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

    @Column(nullable = false, length = 5)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 500)
    private String password;

    @Column(nullable = false, length = 10)
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

    //=============================================================
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Comment> comments = new ArrayList<>();

    // Member 에 like 를 필드로 가지고 있을 필요 없음
    //=============================================================
    @Builder
    public Member(String name, String email, String password, String nickName, String addr, String birth, String phone, Role roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
        this.roles = roles;
    }

    //======================================================================================
    // 수정
    public void updateMember(String password, String nickName, String addr, String birth, String phone) {
        this.password = password;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
    }

    public void resetPassword(String newPassword){
        this.password = newPassword;
    }

    // 동물 추가
    public void addPet(Pet pet) {
        pet.setMember(this);
        this.pets.add(pet);
    }

    // 동물 삭제
    public void removePet(Pet pet) {
        pet.setMember(null);
        this.pets.remove(pet);
    }

    //======================================================================================
    // implements Userdetails - 스프링 시큐리티 overrider 메소드
    // 추후에 role 들이 추가될 수 있으므로 ArrayList<>() 로 관리
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE." + this.roles.name()));
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
