package com.project.reservation.service;

import com.project.reservation.dto.request.pet.ReqPet;
import com.project.reservation.dto.response.pet.ResPet;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Pet;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.PetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class PetService {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    public List<ResPet> getPets(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));

        return member.getPets().stream()
                .map(ResPet::fromEntity)
                .collect(Collectors.toList());
    }

//    public List<ResPet> updatePetProfiles(Long memberId, List<ReqPet> reqPets) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
//
//        // 기존 펫들을 Map으로 변환 (ID를 키로 사용)
//        Map<Long, Pet> existingPetsMap = petRepository.findByMemberId(memberId).stream()
//                .collect(Collectors.toMap(Pet::getId, pet -> pet));
//
//        List<Pet> petsToSave = new ArrayList<>();
//
//        for (ReqPet reqPet : reqPets) {
//            if (reqPet.getId() == null) {
//                // 새 펫 추가
//                Pet newPet = ReqPet.ofEntity(reqPet, member);
//                newPet.setMember(member);
//                petsToSave.add(newPet);
//            } else {
//                // 기존 펫 업데이트 또는 새 펫 추가
//                Pet pet = existingPetsMap.getOrDefault(reqPet.getId(), new Pet());
//                pet.updatePet(reqPet.getName(), reqPet.getBreed(), reqPet.getAge());
//                pet.setMember(member);
//                petsToSave.add(pet);
//                existingPetsMap.remove(reqPet.getId());
//            }
//        }
//
//        // 변경된 펫들 저장 (새로운 펫 포함)
//        petRepository.saveAll(petsToSave);
//
//        // 남은 펫들 삭제 (요청에 포함되지 않은 기존 펫)
//        if (!existingPetsMap.isEmpty()) {
//            petRepository.deleteAll(existingPetsMap.values());
//        }
//
//        return member.getPets().stream()
//                .map(ResPet::fromEntity)
//                .collect(Collectors.toList());
//    }
//    @Transactional
//    public List<ResPet> updatePetProfiles(Long memberId, List<ReqPet> reqPets) {
//
//        List<Pet> updatedPets = reqPets.stream()
//                .filter(reqPet -> reqPet.getId() != null)  // null ID 필터링
//                .map(reqPet -> {
//                    log.info("Updating pet with ID: {}", reqPet.getId());
//                    Pet pet = petRepository.findById(reqPet.getId())
//                            .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + reqPet.getId()));
//
//                    pet.updatePet(reqPet.getName(), reqPet.getBreed(), reqPet.getAge());
//                    return pet;
//                })
//                .collect(Collectors.toList());
//
//
//        return updatedPets.stream()
//                .map(ResPet::fromEntity)
//                .collect(Collectors.toList());
//    }
    @Transactional
    public List<ResPet> updatePetProfiles(Long memberId, List<ReqPet> reqPets) {

        // 1. 현재 멤버의 모든 펫을 existingPets 로 대입
        List<Pet> existingPets = petRepository.findByMemberId(memberId);

        // 2. 요청된 펫 ID 목록 생성
        Set<Long> requestedPetIds = reqPets.stream()
                .map(ReqPet::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 요청에 포함되지 않은 펫 삭제
        existingPets.stream()
                .filter(pet -> !requestedPetIds.contains(pet.getId()))
                .forEach(pet -> petRepository.delete(pet));

        // 4. 사용자 엔티티 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 5. 펫 정보 업데이트 또는 새로운 펫 추가
        List<Pet> updatedPets = reqPets.stream()
                .map(reqPet -> {
                    if (reqPet.getId() != null) {
                        // 기존 펫 업데이트
                        return petRepository.findById(reqPet.getId())
                                .map(pet -> {
                                    pet.updatePet(reqPet.getName(), reqPet.getBreed(), reqPet.getAge());
                                    return pet;
                                })
                                .orElseThrow(() -> new RuntimeException("해당 id의 펫이 없습니다. " + reqPet.getId()));
                    } else {
                        // 새로운 펫 추가
                        Pet newPet = Pet.builder()
                                .name(reqPet.getName())
                                .breed(reqPet.getBreed())
                                .age(reqPet.getAge())
                                .member(member)
                                .build();
                        return petRepository.save(newPet);
                    }
                })
                .collect(Collectors.toList());

        return updatedPets.stream()
                .map(ResPet::fromEntity)
                .collect(Collectors.toList());
    }
}