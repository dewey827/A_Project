package com.project.reservation.controller;

import com.project.reservation.dto.request.nonMember.ReqNonMember;
import com.project.reservation.dto.response.nonMember.ResNonMember;
import com.project.reservation.entity.NonMember;
import com.project.reservation.service.NonMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/nonMember")
public class NonMemberController {
    private final NonMemberService nonMemberService;

    @PostMapping("/post")
    public ResponseEntity<?> createNonMember(@RequestBody ReqNonMember reqNonMember) {

        try {
            NonMember savedNonMember = nonMemberService.saveNonMember(reqNonMember);
            return ResponseEntity.ok("예약되었습니다. 곧 관리자가 연락드리겠습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이미 예약된 번호입니다.");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<ResNonMember>> getAllNonMembers() {
        List<NonMember> nonMembers = nonMemberService.getAllNonMembers();
        List<ResNonMember> responses = nonMembers.stream()
                .map(ResNonMember::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
