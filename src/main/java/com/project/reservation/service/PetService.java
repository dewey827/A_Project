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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;


    public ResPet addPet(Long memberId, ReqPet reqPet) {
        Member member = memberRepository.findById(memberId)


        Pet pet = Pet.builder()
                .name(reqPet.getName())
                .breed(reqPet.getBreed())
                .age(reqPet.getAge())
                .member(member)
                .build();

        Pet savedPet = petRepository.save(pet);
        return ResPet.fromEntity(savedPet);
    }

    public List<ResPet> getPetsByMemberId(Long memberId) {
        List<Pet> pets = petRepository.findByMemberId(memberId);
        return pets.stream()
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
