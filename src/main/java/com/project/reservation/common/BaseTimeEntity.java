package com.project.reservation.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate
    @Column(name="created_date", updatable = false)
    private LocalDateTime createdDate;


    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    // 엔티티가 DB 에 저장되기 직전에 호출  비영속 -> 영속상태로 전환되는 시점 이전에 실행
    @PrePersist
    public void onPrePersist(){
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = this.createdDate;
    }

    // 영속상태인 엔티티의 변경사항이 DB 에 반영되기 직전에 호출
    @PreUpdate
    public void onPreUpdate(){
        this.modifiedDate = LocalDateTime.now();
    }
}
