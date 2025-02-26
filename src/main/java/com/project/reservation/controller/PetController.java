package com.project.reservation.controller;

import com.project.reservation.dto.request.pet.ReqPet;
import com.project.reservation.dto.response.pet.ResPet;
import com.project.reservation.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{memberId}/pet")

public class PetController {

    private final PetService petService;

    @GetMapping("/list")
    public ResponseEntity<List<ResPet>> getPets(@PathVariable("memberId") Long memberId) {
        List<ResPet> pets = petService.getPets(memberId);
        return ResponseEntity.ok(pets);
    }

    @PatchMapping("/update")
    public ResponseEntity<List<ResPet>> updatePetProfiles(@PathVariable("memberId") Long memberId, @RequestBody List<ReqPet> reqPets) {
        List<ResPet> updatePets = petService.updatePetProfiles(memberId, reqPets);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatePets);
    }
}