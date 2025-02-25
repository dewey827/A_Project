package com.project.reservation.service;

import com.project.reservation.dto.request.pet.ReqPet;
import com.project.reservation.dto.response.pet.ResPet;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Pet;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;


    public List<ResPet> getPets(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return member.getPets().stream()
                .map(ResPet::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ResPet> addPets(Long memberId, List<ReqPet> reqPets) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<Pet> savedPets = new ArrayList<>();
        for (ReqPet reqPet : reqPets) {
            Pet pet = ReqPet.ofEntity(reqPet, member);
            member.addPet(pet);
            savedPets.add(petRepository.save(pet));
        }

        return savedPets.stream()
                .map(ResPet::fromEntity)
                .collect(Collectors.toList());
    }


    public ResPet updatePet(Long petId, ReqPet reqPet) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        pet.updatePet(reqPet.getName(), reqPet.getBreed(), reqPet.getAge());
        Pet updatedPet = petRepository.save(pet);
        return ResPet.fromEntity(updatedPet);
    }


    // 펫 삭제
    public void deletePet(Long petId) {
        petRepository.deleteById(petId);
    }
}
