package com.project.reservation.dto.response.pet;

import com.project.reservation.entity.Breed;
import com.project.reservation.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ResPet {
    private Long id;
    private String name;
    private Breed breed;
    private int age;

    @Builder
    public ResPet(Long id,String name, Breed breed, int age) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.age = age;
    }

    public static ResPet fromEntity(Pet pet) {
        return ResPet.builder()
                .id(pet.getId())
                .name(pet.getName())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .build();
    }
}