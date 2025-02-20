package com.project.reservation.dto.request.pet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReqPet {

    private String name;
    private String breed;
    private int age;
    private double weight;

    @Builder
    public ReqPet(String name, String breed, int age, double weight) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
    }
}
