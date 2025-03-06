package com.project.reservation.dto.request.pet;

import com.project.reservation.entity.Breed;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReqPet {
    private Long id;
    private String name;
    private Breed breed;
    private int age;

    @Builder
    public ReqPet(Long id, String name, Breed breed, int age) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.age = age;
    }

    public static Pet ofEntity(ReqPet reqPet, Member member) {
        return Pet.builder()
                .id(reqPet.getId())
                .name(reqPet.getName())
                .breed(reqPet.getBreed())
                .age(reqPet.getAge())
                .member(member)
                .build();
    }
}