package com.project.reservation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Pet {

    @Id @GeneratedValue
    @Column(name = "PET_ID")
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false)
    private String breed;

    @Column(nullable = false)
    private int age;

    //======================================================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    public Member member;

    //======================================================================================
    @Builder
    public Pet( Long id, String name, String breed, int age, Member member) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.member = member;
    }

    //======================================================================================
    // 연관관계 메소드
    public void setMember(Member member) {
        this.member = member;
        member.getPets().add(this);
    }

    //동물정보 수정
    public void updatePet(String name, String breed, int age) {
        this.name = name;
        this.breed = breed;
        this.age = age;
    }
}