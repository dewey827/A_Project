package com.project.reservation.service;

import com.project.reservation.dto.request.nonMember.ReqNonMember;
import com.project.reservation.entity.NonMember;
import com.project.reservation.repository.NonMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class NonMemberService {
    private final NonMemberRepository nonMemberRepository;

    public NonMember saveNonMember(ReqNonMember reqNonMember) {
        return nonMemberRepository.save(ReqNonMember.ofEntity(reqNonMember));
    }

    public List<NonMember> getAllNonMembers() {
        return nonMemberRepository.findAll();
    }

}
