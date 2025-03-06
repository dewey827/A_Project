package com.project.reservation.controller;

import com.project.reservation.dto.request.pet.ReqPet;
import com.project.reservation.dto.response.pet.ResPet;
import com.project.reservation.entity.Member;
import com.project.reservation.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")

public class PetController {

    private final PetService petService;

    @GetMapping
    public ResponseEntity<List<ResPet>> getPets(@AuthenticationPrincipal Member member) {
        List<ResPet> pets = petService.getPets(member.getId());
        return ResponseEntity.ok(pets);
    }

    @PatchMapping
    public ResponseEntity<List<ResPet>> updatePetProfiles(@AuthenticationPrincipal Member member, @RequestBody List<ReqPet> reqPets) {
        List<ResPet> updatePets = petService.updatePetProfiles(member.getId(), reqPets);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatePets);
    }
}