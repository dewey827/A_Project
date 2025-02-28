package com.project.reservation.repository;

import com.project.reservation.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickName(String nickName);

    Optional<Member> findByNameAndPhone(String name, String phone);

//    Optional<Member> findByUserName(String userName);
}