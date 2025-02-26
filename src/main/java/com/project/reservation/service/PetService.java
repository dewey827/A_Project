package com.project.reservation.service;

import com.project.reservation.dto.request.pet.ReqPet;
import com.project.reservation.dto.response.pet.ResPet;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Pet;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public List<ResPet> updatePetProfiles(Long memberId, List<ReqPet> reqPets) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        // 기존 펫들을 Map으로 변환 (ID를 키로 사용)
        Map<Long, Pet> existingPetsMap = petRepository.findByMemberId(memberId).stream()
                .collect(Collectors.toMap(Pet::getId, pet -> pet));

        List<Pet> petsToSave = new ArrayList<>();

        for (ReqPet reqPet : reqPets) {
            if (reqPet.getId() == null) {
                // 새 펫 추가
                Pet newPet = ReqPet.ofEntity(reqPet, member);
                newPet.setMember(member);
                petsToSave.add(newPet);
            } else {
                // 기존 펫 업데이트 또는 새 펫 추가
                Pet pet = existingPetsMap.getOrDefault(reqPet.getId(), new Pet());
                pet.updatePet(reqPet.getName(), reqPet.getBreed(), reqPet.getAge());
                pet.setMember(member);
                petsToSave.add(pet);
                existingPetsMap.remove(reqPet.getId());
            }
        }

        // 변경된 펫들 저장 (새로운 펫 포함)
        petRepository.saveAll(petsToSave);

        // 남은 펫들 삭제 (요청에 포함되지 않은 기존 펫)
        if (!existingPetsMap.isEmpty()) {
            petRepository.deleteAll(existingPetsMap.values());
        }

        return member.getPets().stream()
                .map(ResPet::fromEntity)
                .collect(Collectors.toList());
    }
}