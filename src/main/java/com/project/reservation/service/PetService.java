package com.project.reservation.service;

import com.project.reservation.entity.Member;
import com.project.reservation.entity.Pet;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 모든 펫 조회
    public List<Pet> findAllPets() {
        return petRepository.findAll();
    }

    // ID로 펫 조회
    public Pet findPetById(Long petId) {
        return petRepository.findById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    // 회원에게 펫 추가
    @Transactional
    public void addPetToMember(Long memberId, Pet pet) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        pet.setMember(member);
        petRepository.save(pet);
    }

    // 펫 정보 수정
    @Transactional
    public void updatePet(Long petId, String name, String breed, int age, double weight) {
        Pet pet = findPetById(petId);
        pet.updatePet(name, breed, age, weight);
        petRepository.save(pet);
    }

    // 회원에게서 펫 제거
    @Transactional
    public void removePet(Long petId) {
        Pet pet = findPetById(petId);
        pet.setMember(null);
        petRepository.delete(pet);
    }
}
