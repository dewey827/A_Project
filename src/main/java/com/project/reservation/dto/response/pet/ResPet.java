package com.project.reservation.dto.response.pet;

import com.project.reservation.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ResPet {

    private String name;
    private String breed;
    private int age;
    private double weight;

    @Builder
    public ResPet(String name, String breed, int age, double weight) {

        this.name = name;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
    }

    public static ResPet fromEntity(Pet pet) {
        return ResPet.builder()
                .name(pet.getName())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .weight(pet.getWeight())
                .build();
    }
}
