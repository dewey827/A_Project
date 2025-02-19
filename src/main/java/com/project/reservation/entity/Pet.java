package com.project.reservation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity

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

    @Column(nullable = false)
    private double weight;

    //======================================================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    public Member member;

    //======================================================================================
    @Builder
    public Pet( Long id, String name, String breed, int age, double weight, Member member) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.member = member;
    }

    //======================================================================================
    //동물정보 수정
    public void updatePet(String name, String breed, int age, double weight) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
    }


}
