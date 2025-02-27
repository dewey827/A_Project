package com.project.reservation.entity;

import com.project.reservation.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class DeletedMember extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column
    private Long originalId;

    @Column(nullable = false, length = 5)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(length = 500)
    private String password;

    @Column(nullable = false, length = 10)
    private String nickName;

    @Column(nullable = false, length = 50)
    private String addr;

    @Column(nullable = false, length = 8)
    private String birth;

    @Column(length = 13)
    private String phone;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public DeletedMember(Long id, Long originalId, String name, String email, String password, String nickName, String addr, String birth, String phone, LocalDateTime deletedAt) {
        this.id = id;
        this.originalId = originalId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
        this.deletedAt = deletedAt;
    }
}
